package ut;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.infra.events.StoreSerdes;

/**
 * This is an example of counting the number time a given item is sold cross stores.
 * It uses Aggregations and caching to state store
 */
public class TestAccumulateItemSoldWithCaching {
    private  static TopologyTestDriver testDriver;
    private  static TestInputTopic<String, ItemTransaction> inputTopic;
    private  static TestOutputTopic<String, Long> outputTopic;
    private static String inTopicName = "my-input-topic";
    private static String outTopicName = "my-output-topic";
    private static String storeItemTableName;

    public static Topology buildTopologyFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
         // 1- get the input stream
         KStream<String,ItemTransaction> items = builder.stream(inTopicName, 
         Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde())); 
         // 2- to compute aggregate we need to group records by key to create KGroupTable 
         // Here the key is the storeName
         KTable<String,Long> countedItems = items
         .filter((k,v) -> ItemTransaction.SALE.equals(v.type))
         .toTable()
         // group by SKU
         .groupBy((k,v) -> {return new KeyValue<String,ItemTransaction>(v.sku,v);},
            Grouped.with(Serdes.String(), StoreSerdes.ItemTransactionSerde()))
         // 3- change the stream type from KGroupedTable<String, ItemTransaction> to KTable<String, Long>
         .count();
        storeItemTableName = countedItems.queryableStoreName();
        return builder.build();  
    }


    @BeforeAll
    public static void setup() { 
        Topology topology = buildTopologyFlow();
        System.out.println(topology.describe());
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inputTopic = testDriver.createInputTopic(inTopicName, new StringSerializer(), StoreSerdes.ItemTransactionSerde().serializer());
        outputTopic = testDriver.createOutputTopic(outTopicName, new StringDeserializer(),  new LongDeserializer());
        
    }

    public  static Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstream-labs");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummmy:1234");
        //  controls the number of bytes allocated for caching. there are as many caches as there are threads, but no sharing of caches across threads happens.
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 1 * 1024 * 1024L);
        // Set commit interval to 1 second.
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,   StoreSerdes.ItemTransactionSerde().getClass());
        return props;
    }

    @AfterAll
    public  static void tearDown() {
        try {
            testDriver.close();
        } catch (final Exception e) {
             System.out.println("Ignoring exception, test failing due this exception:" + e.getLocalizedMessage());
        } 
    }

    /**
     * If we do not send message to the input topic there is no message to the output topic.
     */
    @Test
    public void isEmpty() {
        assertThat(outputTopic.isEmpty(), is(true));
    }

    @Test
    public void shouldExpectCountingSaleEventsPerSKU(){
        ItemTransaction item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,25,33.2);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,10,33.2);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-1","Item-2",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.storeName, item);

        // there is no output topic, but a state store so let play with it
        ReadOnlyKeyValueStore<String,Long> inventory = testDriver.getKeyValueStore(storeItemTableName);
        Assertions.assertEquals(2L,  inventory.get("Item-1"));
      
        // Still the ktable is in memory so a new item sold should generate a new count
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.storeName, item);
        Assertions.assertEquals(3L,  inventory.get("Item-1"));
    }
}
