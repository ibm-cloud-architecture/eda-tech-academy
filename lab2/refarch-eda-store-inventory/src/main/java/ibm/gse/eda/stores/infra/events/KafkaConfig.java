package ibm.gse.eda.stores.infra.events;

import java.util.Optional;
import java.util.Properties;

import javax.inject.Singleton;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.annotations.ConfigItem;

/**
 * Keep Kafka Configuration loaded from properties or env. variable
 */
@Singleton
public class KafkaConfig {

    /**
     * Default Kafka bootstrap server.
     */
    public static final String DEFAULT_KAFKA_BROKER = "localhost:9012";

    /**
     * A unique identifier for this Kafka Streams application.
     * If not set, defaults to quarkus.application.name.
     */
    @ConfigItem(defaultValue = "${quarkus.application.name}")
    public String applicationId;

    @ConfigProperty(name="kafka.bootstrap.servers")
    protected  String bootstrapServers; 
    @ConfigProperty(name="application.id",defaultValue = "StoreAggregator")
    protected Optional<String> applicationID;
    @ConfigItem(defaultValue = "schema.registry.url")
    public String schemaRegistryKey;

    /**
     * The schema registry URL.
     */
    @ConfigItem
    public Optional<String> schemaRegistryUrl;

    /**
     * A unique identifier of this application instance, typically in the form host:port.
     */
    @ConfigItem
    public Optional<String> applicationServer;
    
    public KafkaConfig(){}

    public Properties getKafkaProperties(){
        Properties properties = KafkaPropertiesUtil.appKafkaProperties();
        properties.putAll(KafkaPropertiesUtil.quarkusKafkaProperties());
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //properties.put(StreamsConfig.APPLICATION_ID_CONFIG,properties.get(key));
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return properties;
    }
}

