package ut;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

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

public class TestYourFirstTopology {
    
    private  static TopologyTestDriver testDriver;
    private static String inTopicName = "my-input-topic";
    private static String outTopicName = "my-output-topic";
    private static TestInputTopic<String, String> inTopic;
    private static TestOutputTopic<String, String> outTopic;

    public static Topology buildTopologyFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> basicColors = builder.stream(inTopicName,Consumed.with(Serdes.String(), Serdes.String()));
        basicColors.peek((key, value) -> System.out.println("PRE-FILTER: key=" + key + ", value=" + value))
            .filter((key, value) -> ("BLUE".equalsIgnoreCase(value)))
            .peek((key, value) -> System.out.println("POST-FILTER: key=" + key + ", value=" + value))
            .to(outTopicName);
        return builder.build();  
    }


    @BeforeAll
    public static void setup() { 
        Topology topology = buildTopologyFlow();
        System.out.println(topology.describe());
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inTopic = testDriver.createInputTopic(inTopicName, new StringSerializer(), new StringSerializer());
        outTopic = testDriver.createOutputTopic(outTopicName, new StringDeserializer(), new StringDeserializer());
        
    }

    public  static Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstream-labs");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummmy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,  Serdes.String().getClass());
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
        assertThat(outTopic.isEmpty(), is(true));
    }

    /**
     * Send one message, and use test topic to assess expected results
     */
    @Test
    public void isNotEmpty() {
        assertThat(outTopic.isEmpty(), is(true));
        inTopic.pipeInput("C01", "blue");
        assertThat(outTopic.getQueueSize(), equalTo(1L) );
        assertThat(outTopic.readValue(), equalTo("blue"));
        assertThat(outTopic.getQueueSize(), equalTo(0L) );
    }


    /**
     * Do the real test of the topology
     */
    @Test
    public void shouldGetOnlyTheBlue() {
        assertThat(outTopic.isEmpty(), is(true));
        inTopic.pipeInput("C01", "blue");
        inTopic.pipeInput("C02", "red");
        inTopic.pipeInput("C03", "green");
        inTopic.pipeInput("C04", "Blue");
        assertThat(outTopic.getQueueSize(), equalTo(2L) );
        assertThat(outTopic.isEmpty(), is(false));
        assertThat(outTopic.readValue(), equalTo("blue"));
        assertThat(outTopic.readValue(), equalTo("Blue"));
        assertThat(outTopic.getQueueSize(), equalTo(0L) );
    }
}
