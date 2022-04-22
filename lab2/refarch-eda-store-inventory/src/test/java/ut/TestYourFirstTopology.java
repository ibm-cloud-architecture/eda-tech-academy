package ut;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.infra.events.StoreSerdes;

public class TestYourFirstTopology {
    // Name of input topic
    public static String itemSoldInputStreamName = "items";
    
    private  TopologyTestDriver testDriver;

    private  TestInputTopic<String, ItemTransaction> inputTopic;

    public static Topology buildTopologyFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String,ItemTransaction> items = builder.stream(itemSoldInputStreamName, 
        Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde()));
        return builder.build();  
    }
    @BeforeEach
    public void setup() { 
        Topology topology = buildTopologyFlow();
        System.out.println(topology.describe());
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inputTopic = testDriver.createInputTopic(itemSoldInputStreamName, 
                                new StringSerializer(),
                                StoreSerdes.ItemTransactionSerde().serializer());
        
    }

    public  static Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "store-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummmy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,  Serdes.String().getClass());
        return props;
    }

    @AfterEach
    public  void tearDown() {
        try {
            testDriver.close();
        } catch (final Exception e) {
             System.out.println("Ignoring exception, test failing due this exception:" + e.getLocalizedMessage());
        } 
    }

    /**
     * Giving 3 events representing item transaction from two different stores
     * only print store-1 events
     */
    @Test
    public void firstTest(){
        // Send events to the input topic
        ItemTransaction item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,5,33.2);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-2","Item-3",ItemTransaction.RESTOCK,15,10.0);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-1","Item-2",ItemTransaction.RESTOCK,10,50.0);
        inputTopic.pipeInput(item.storeName, item);
      
    }
}
