package ut;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.infra.events.StoreSerdes;

/**
 * This is an example of counting the number time a given item is sold cross stores.
 * It uses Aggregations.
 * Aggregations are key-based operations: they always operate over records (notably record values) of the same key. 
 */
public class TestAccumulateItemSold {
    private  static TopologyTestDriver testDriver;
    private  static TestInputTopic<String, ItemTransaction> inputTopic;
    private  static TestOutputTopic<String, Long> outputTopic;
    private static String inTopicName = "my-input-topic";
    private static String outTopicName = "my-output-topic";

    public static Topology buildTopologyFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
         // 1- get the input stream
         KStream<String,ItemTransaction> items = builder.stream(inTopicName, 
         Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde())); 
         // 2- to compute aggregate we need to group records by key to create KGroupTable or stream
         // here we group the records by their current key into a KGroupedStream 
         KTable<String,Long> countedItems = items
         .filter((k,v) -> ItemTransaction.SALE.equals(v.type))
         .groupByKey()
         // 3- change the stream type from KGroupedStream<String, ItemTransaction> to KTable<String, Long>
         .count();
         
        // Generate to output topic
        countedItems.toStream().to(outTopicName);
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
        inputTopic.pipeInput(item.sku, item);
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,10,33.2);
        inputTopic.pipeInput(item.sku, item);
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.sku, item);
        item = new ItemTransaction("Store-1","Item-2",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.sku, item);
        Long countedRecord = outputTopic.readValue();
        // first record generate 0 message, but second record is a SALE so count == 1
        Assertions.assertEquals(1L,  countedRecord);
        countedRecord = outputTopic.readValue();
        // SALE on same item so count == 2
        Assertions.assertEquals(2L,  countedRecord);
        // SALE on new item so count == 1
        countedRecord = outputTopic.readValue();
        Assertions.assertEquals(1L,  countedRecord);
        assertThat(outputTopic.isEmpty(), is(true));
        // Still the ktable is in memory so a new item sold should generate a new count
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,1,33.2);
        inputTopic.pipeInput(item.sku, item);
        Assertions.assertEquals(3L,  outputTopic.readValue());
    }
}
