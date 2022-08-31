package ibm.swat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

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
    public static Properties loadProducerConfigFromProperties(String[] args){
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

        consumerConfig.put("group.id", config.getProperty("group.id"));
        consumerConfig.put("client.id",  config.getProperty("client.id"));
        consumerConfig.put("auto.commit.enable", "false");
        consumerConfig.put("auto.offset.reset", "earliest");
	    consumerConfig.setProperty("key.deserializer", StringDeserializer.class.getName());
        consumerConfig.setProperty("specific.avro.reader", "true");
        return consumerConfig;
    }

    private static Properties buildCommonProperties(Properties config) {
        Properties commonConfig = new Properties();
        commonConfig.setProperty("bootstrap.servers", config.getProperty("bootstrap.servers"));
       
        commonConfig.setProperty("security.protocol", config.getProperty("security.protocol"));
        commonConfig.setProperty("sasl.jaas.config", config.getProperty("sasl.jaas.config"));
        commonConfig.setProperty("sasl.mechanism", config.getProperty("sasl.mechanism"));
       
       if (config.getProperty("security.protocol").contains("SSL")) {
            commonConfig.setProperty("ssl.truststore.location", config.getProperty("ssl.truststore.location"));  
            commonConfig.setProperty("ssl.endpoint.identification.algorithm", "");
            if (config.getProperty("ssl.truststore.type").contains("PKCS12") || config.getProperty("ssl.truststore.type").contains("JKS")) {
                commonConfig.setProperty("ssl.truststore.password",  config.getProperty("ssl.truststore.password"));
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
            commonConfig.setProperty("basic.auth.credentials.source", config.getProperty("basic.auth.credentials.source"));
            commonConfig.setProperty("schema.registry.basic.auth.user.info", config.getProperty("schema.registry.basic.auth.user.info"));
        	commonConfig.setProperty("schema.registry.ssl.endpoint.identification.algorithm", "");
			
            commonConfig.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
            commonConfig.setProperty("key.serializer", KafkaAvroSerializer.class.getName());
         
            if (config.getProperty("schema.registry.url").contains("https")) {
	            commonConfig.setProperty("schema.registry.ssl.truststore.location", config.getProperty("schema.registry.ssl.truststore.location"));
                commonConfig.setProperty("schema.registry.ssl.truststore.password", config.getProperty("schema.registry.ssl.truststore.password"));
                commonConfig.setProperty("schema.registry.ssl.endpoint.identification.algorithm", "");
            }
        } else {
            commonConfig.setProperty("value.serializer", StringSerializer.class.getName());
            commonConfig.setProperty("key.serializer", StringSerializer.class.getName());
        }
        if (config.getProperty("sasl.mechanism").equals("GSSAPI")) {
            commonConfig.setProperty("sasl.kerberos.service.name", config.getProperty("sasl.kerberos.service.name"));
        }
        // properties.setProperty("sasl.kerberos.service.name", kerberosservicename);
        // avro part
        commonConfig.setProperty("key.serializer", StringSerializer.class.getName());

        if (Boolean.parseBoolean(config.getProperty("enableintercept"))) {
            commonConfig.setProperty("interceptor.classes",
                    "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
                    commonConfig.setProperty("confluent.monitoring.interceptor.security.protocol",  config.getProperty("intercept_security"));
                    commonConfig.setProperty("confluent.monitoring.interceptor.sasl.mechanism",  config.getProperty("intercept_saslmechanism"));
                    commonConfig.setProperty("confluent.monitoring.interceptor.sasl.jaas.config", config.getProperty("intercept_sasljaas"));
                    commonConfig.setProperty("confluent.monitoring.interceptor.bootstrap.servers", config.getProperty("intercept_bootstrapServers"));
        }
		return commonConfig;
    }
}
