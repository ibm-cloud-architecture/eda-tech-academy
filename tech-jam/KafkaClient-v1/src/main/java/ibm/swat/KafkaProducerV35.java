package ibm.swat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaProducerV35 {
	private static String file1Name = "file1.txt";
	private static String file2Name = "file2.txt";
	private static String file3Name = "file3.txt";
	private static int numofrecords = 0;
	public static Producer<String, Customer> producer;

	public void start(Properties config) {
		producer = new KafkaProducer<>(config);
		config.forEach((k,v) -> {System.out.println(k.toString() + "\t" + v.toString());});
		List<String> file1lines = loadDataFile(file1Name);
		List<String> file2lines = loadDataFile(file2Name);;
		List<String> file3lines = loadDataFile(file3Name);
		

		// These parameters are in Customer.java which was generated from customer.avsc
		for (int i = 1; i <= KafkaConfig.loadsize; i++) {
			int random1WordIndex = 1 + (int) (Math.random() * ((file1lines.size() - 1)));
			int random2WordIndex = 1 + (int) (Math.random() * ((file2lines.size() - 1)));
			int random3WordIndex = 1 + (int) (Math.random() * ((file3lines.size() - 1)));
			Customer customer = Customer.newBuilder()
						.setAge((int) (Math.random() * ((80 - 1) + 1)) + 1)
						.setFirstName(file1lines.get(random1WordIndex))
						.setLastName(file2lines.get(random2WordIndex))
						.setCountry(file3lines.get(random3WordIndex))
						.setHeight((float) (Math.random() * ((170 - 60) + 1)) + 60)
						.setWeight((float) (Math.random() * ((120 - 10) + 1)) + 10)
						.build();

			ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(
					KafkaConfig.topic, customer);
				producer.send(producerRecord, new Callback() {
					@Override
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if (exception == null) {
							numofrecords = numofrecords + 1;
							System.out.printf(
									"Count: [%d], Topic: [%s], Partition: [%d], Offset: [%d], Message: [%s]%n",
									numofrecords, metadata.topic(), metadata.partition(), metadata.offset(), customer);
						} else {
							exception.printStackTrace();
						}
					}
				});
		
		} // end for loop
		producer.flush();
		producer.close();
		
	}

	private List<String> loadDataFile(String fileName){
		List<String> fileLines = new ArrayList<String>();
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
			if (inputStream == null) {
				throw new IllegalArgumentException("file not found! " + fileName);
			}

			InputStreamReader streamReader =
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            
			BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
			}
		} catch (IOException e) {
			System.err.println(fileName + " file can't be opened.");
		}
		return fileLines;
	}
}
