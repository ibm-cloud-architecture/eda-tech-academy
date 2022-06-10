package ibm.gse.eda.stores.infra.events;

import java.util.Optional;
import java.util.Properties;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class KafkaPropertiesUtil {

    private static final String KAFKA_OPTION_PREFIX = "kafka.";
   
    private static boolean isKafkaProperty(String prefix, String property) {
        return property.startsWith(prefix);
    }

    private static void includeKafkaStreamsProperty(Config config, Properties kafkaStreamsProperties, String prefix,
            String property) {
        Optional<String> value = config.getOptionalValue(property, String.class);
        if (value.isPresent()) {
            kafkaStreamsProperties.setProperty(property.substring(prefix.length()), value.get());
        }
    }


    private static Properties defineKafkaPropertiesUsingPrefix(String prefix) {
        Properties kafkaStreamsProperties = new Properties();
        Config config = ConfigProvider.getConfig();
        for (String property : config.getPropertyNames()) {
            if (isKafkaProperty(prefix, property)) {
                includeKafkaStreamsProperty(config, kafkaStreamsProperties, prefix, property);
            }
        }

        return kafkaStreamsProperties;
    }

    public static Properties appKafkaProperties() {
        return defineKafkaPropertiesUsingPrefix(KAFKA_OPTION_PREFIX);
    }

}
