package ibm.swat;

import java.util.Properties;

public class KafkaClientV35 {
    public static void main(String[] args) throws Exception {
        if (args != null && args.length > 0) {
            String option = args[0];
            String[] args2 = new String[0];

            if (args.length > 1) {
                args2 = new String[args.length - 1];
                System.arraycopy(args, 1, args2, 0, args2.length);
            }


            if (option.equalsIgnoreCase("producer")) {
                Properties config = KafkaConfig.loadProducerConfigFromProperties(args2);
                new KafkaProducerV35().start(config);
            } else if (option.equalsIgnoreCase("consumer")) {
                Properties config = KafkaConfig. loadConsumerConfigFromProperties(args2);
                new KafkaConsumerV35().start(config);
            } else {
                System.out.println(
                        "Usage For Producer: java -jar KafkaClient.jar producer <number_of_records> <config_file>");
                System.out.println("Usage For Consumer: java -jar KafkaClient.jar consumer <config_file>\n");
            }
        } else {
            System.out.println(
                    "Usage For Producer: java -jar KafkaClient.jar producer <number_of_records> <config_file>");
            System.out.println("Usage For Consumer: java -jar KafkaClient.jar consumer <config_file>\n");
        }
    }
}
