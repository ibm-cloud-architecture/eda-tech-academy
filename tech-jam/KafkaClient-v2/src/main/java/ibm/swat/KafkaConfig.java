package ibm.swat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class KafkaConfig {
    private static String propertiesfile = "./config.properties";
    public static int loadsize = 1;

    public KafkaConfig() {
    }

    public static String topic = "customers";

    /**
     * Load config properties files and build properties for the Kafka Producer only
     * depending of the Security settings
     * return the properties for Kafka producer
     */
    public static Properties loadProducerConfigFromProperties(String[] args) {
        if (args.length != 2) {
            System.out.println("ERROR: Number_of_Records parameter required");
            System.exit(0);
        } else {
            loadsize = Integer.parseInt(args[0]);
            propertiesfile = args[1];
        }
        Properties config = loadConfigFile(propertiesfile);
        topic = config.getProperty("topic");

        Properties producerConfig = buildCommonProperties(config);
        producerConfig.setProperty("acks", "all");
        producerConfig.setProperty("retries", config.getProperty("retries"));
        producerConfig.setProperty("key.serializer", config.getProperty("key.serializer"));
        producerConfig.setProperty("value.serializer", config.getProperty("value.serializer"));
        return producerConfig;
    }

    private static Properties loadConfigFile(String propertiesfile) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(propertiesfile)) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }

    public static Properties loadConsumerConfigFromProperties(String[] args) {
        if (args.length == 1) {
            propertiesfile = args[0];
        }
        Properties config = loadConfigFile(propertiesfile);
        Properties consumerConfig = buildCommonProperties(config);
        topic = config.getProperty("topic");
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, config.getProperty("group.id"));
        consumerConfig.put("client.id", config.getProperty("client.id"));
        consumerConfig.put("auto.commit.enable", "false");
        consumerConfig.put("auto.offset.reset", "earliest");
        consumerConfig.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, config.getProperty("key.deserializer"));
        consumerConfig.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, config.getProperty("value.deserializer"));
        if (Boolean.parseBoolean(config.getProperty("enableschemaavro"))) {
            consumerConfig.setProperty("specific.avro.reader", "true");
        }
        return consumerConfig;
    }

    private static Properties buildCommonProperties(Properties config) {
        Properties commonConfig = new Properties();
        commonConfig.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("bootstrap.servers"));

        commonConfig.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getProperty("security.protocol"));
        commonConfig.setProperty(SaslConfigs.SASL_JAAS_CONFIG, config.getProperty("sasl.jaas.config"));
        commonConfig.setProperty(SaslConfigs.SASL_MECHANISM, config.getProperty("sasl.mechanism"));

        if (config.getProperty("security.protocol").contains("SSL")) {
            commonConfig.setProperty(SslConfigs.SSL_PROTOCOL_CONFIG,"TLSv1.2");
            commonConfig.setProperty(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, config.getProperty("ssl.truststore.type"));
            commonConfig.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,config.getProperty("ssl.truststore.location"));
            commonConfig.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
            commonConfig.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
            if (config.getProperty("ssl.truststore.type").contains("PKCS12")
                    || config.getProperty("ssl.truststore.type").contains("JKS")) {
                commonConfig.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getProperty("ssl.truststore.password"));
            }

        }

        if (Boolean.parseBoolean(config.getProperty("enablemtls"))) {
            commonConfig.setProperty("ssl.keystore.location", config.getProperty("ssl.keystore.location"));
            commonConfig.setProperty("ssl.keystore.password", config.getProperty("ssl.keystore.password"));
            commonConfig.setProperty("ssl.key.password", config.getProperty("ssl.key.password"));
        }

        if (Boolean.parseBoolean(config.getProperty("enableschemaavro"))) {
            commonConfig.setProperty("auto.register.schemas", config.getProperty("auto.register.schemas"));
            commonConfig.setProperty("schema.registry.url", config.getProperty("schema.registry.url"));
            commonConfig.setProperty("basic.auth.credentials.source",
                    config.getProperty("basic.auth.credentials.source"));
            commonConfig.setProperty("schema.registry.basic.auth.user.info",
                    config.getProperty("schema.registry.basic.auth.user.info"));
            commonConfig.setProperty("schema.registry.ssl.endpoint.identification.algorithm", "");

            if (config.getProperty("schema.registry.url").contains("https")) {
                commonConfig.setProperty("schema.registry.ssl.truststore.location",
                        config.getProperty("schema.registry.ssl.truststore.location"));
                commonConfig.setProperty("schema.registry.ssl.truststore.password",
                        config.getProperty("schema.registry.ssl.truststore.password"));
                commonConfig.setProperty("schema.registry.ssl.truststore.type",
                        config.getProperty("ssl.truststore.type"));
                commonConfig.setProperty("schema.registry.ssl.endpoint.identification.algorithm", "https");
            }
        }

        if (config.getProperty("sasl.mechanism").equals("GSSAPI")) {
            commonConfig.setProperty("sasl.kerberos.service.name", config.getProperty("sasl.kerberos.service.name"));
        }

        if (Boolean.parseBoolean(config.getProperty("enableintercept"))) {
            commonConfig.setProperty("interceptor.classes",
                    "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
            commonConfig.setProperty("confluent.monitoring.interceptor.security.protocol",
                    config.getProperty("intercept_security"));
            commonConfig.setProperty("confluent.monitoring.interceptor.sasl.mechanism",
                    config.getProperty("intercept_saslmechanism"));
            commonConfig.setProperty("confluent.monitoring.interceptor.sasl.jaas.config",
                    config.getProperty("intercept_sasljaas"));
            commonConfig.setProperty("confluent.monitoring.interceptor.bootstrap.servers",
                    config.getProperty("intercept_bootstrapServers"));
        }
        return commonConfig;
    }
}
