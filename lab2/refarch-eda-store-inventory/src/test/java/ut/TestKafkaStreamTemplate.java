package ut;

import java.util.Properties;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.infra.events.StoreSerdes;

/**
 * This a code template to reuse to define new test suite.
 */
public class TestKafkaStreamTemplate {
    private  static TopologyTestDriver testDriver;
    private  static TestInputTopic<String, ItemTransaction> inputTopic;
    private  static TestOutputTopic<String, ItemTransaction> outputTopic;
    private static String inTopicName = "my-input-topic";
    private static String outTopicName = "my-output-topic";

    public static Topology buildTopologyFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
         // 1- get the input stream
         KStream<String,ItemTransaction> items = builder.stream(inTopicName, 
         Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde()));  
        // 2 processing

        // Generate to output topic
        items.to(outTopicName);
        return builder.build();  
    }


    @BeforeAll
    public static void setup() { 
        Topology topology = buildTopologyFlow();
        System.out.println(topology.describe());
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inputTopic = testDriver.createInputTopic(inTopicName, new StringSerializer(), StoreSerdes.ItemTransactionSerde().serializer());
        outputTopic = testDriver.createOutputTopic(outTopicName, new StringDeserializer(),  StoreSerdes.ItemTransactionSerde().deserializer());
        
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
}
