package ibm.gse.eda.stores.infra.events;

import java.util.Optional;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Keep Kafka Configuration loaded from properties or env. variable
 */
@ApplicationScoped
public class KafkaConfig {

    /**
     * Default Kafka bootstrap server.
     */
    public static final String DEFAULT_KAFKA_BROKER = "localhost:9012";

    /**
     * A unique identifier for this Kafka Streams application.
     * If not set, defaults to quarkus.application.name.
     */
    @Inject
    @ConfigProperty(name="kafka.bootstrap.servers")
    private  String bootstrapServers; 
    @Inject
    @ConfigProperty(name="application.id")
    private Optional<String> applicationID;
    @Inject
    @ConfigProperty(name = "schema.registry.key")
    private Optional<String> schemaRegistryKey;

    /**
     * The schema registry URL.
     */
    @Inject
    @ConfigProperty(name = "schema.registry.url")
    private Optional<String> schemaRegistryUrl;

    /**
     * A unique identifier of this application instance, typically in the form host:port.
     */
    @Inject
    @ConfigProperty
    private Optional<String> applicationServer;
    
    public KafkaConfig(){}

    public Properties getKafkaProperties(){
        Properties properties = KafkaPropertiesUtil.appKafkaProperties();
        properties.putAll(KafkaPropertiesUtil.appKafkaProperties());
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //properties.put(StreamsConfig.APPLICATION_ID_CONFIG,properties.get(key));
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return properties;
    }
}

