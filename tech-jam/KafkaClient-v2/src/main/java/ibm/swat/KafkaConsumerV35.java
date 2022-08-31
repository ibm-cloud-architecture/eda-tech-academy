package  ibm.swat;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerV35 {
   
    public static KafkaConsumer<String, Customer> avroConsumer;
   

    public static void start(Properties config) {
	    int count = 1;

        avroConsumer = new KafkaConsumer<>(config);
    	avroConsumer.subscribe(Collections.singleton(KafkaConfig.topic));
	
        config.forEach((k,v) -> {System.out.println(k.toString() + "\t" + v.toString());});

        
        while (true){
            System.out.println("Polling");
	   
            ConsumerRecords<String, Customer> records = avroConsumer.poll(Duration.ofMillis(1000));
           	for (ConsumerRecord<String, Customer> record : records){
                	Customer customer = record.value();
			        System.out.println("Count: " + count);
                	System.out.println(customer);
			        count=count+1;  
            }
	
            avroConsumer.commitSync();
	    }
	    
    }
}
