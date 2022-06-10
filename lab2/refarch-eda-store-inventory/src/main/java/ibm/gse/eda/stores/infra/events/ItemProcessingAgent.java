package ibm.gse.eda.stores.infra.events;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.microprofile.health.Startup;

import ibm.gse.eda.stores.domain.StoreInventoryAggregator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;

/**
 * The agent is here to do the execution of the aggregator
 */
@ApplicationScoped
public class ItemProcessingAgent {
    private static final Logger logger = Logger.getLogger(ItemProcessingAgent.class.getName());
    private static volatile boolean shutdown = false;
    private final ExecutorService executorService;
    
    
   
    private KafkaStreams kafkaStreams;

    @Inject
    private KafkaConfig kafkaConfig;
    @Inject
    private StoreInventoryAggregator aggregator;

    public ItemProcessingAgent() {  
        this.executorService = Executors.newSingleThreadExecutor();
    }


    public synchronized void stop() {
       
    }
    

    void onStart(@Observes @Initialized(ApplicationScoped.class)Object context){
        this.kafkaStreams = initializeKafkaStreams();
		logger.info("ItemProcessingAgent started");
     }
 
     void onStop(@Observes @BeforeDestroyed(ApplicationScoped.class)  Object ev){
        shutdown = true;
        if (executorService != null) {
            executorService.shutdown();
        }
        if ( kafkaStreams != null ) {
            kafkaStreams.close();
        }
      }

      @Produces
      @Singleton
      @Startup
      public KafkaStreams getKafkaStreams() {
          return kafkaStreams;
      }

      private KafkaStreams initializeKafkaStreams() {
        Properties props = kafkaConfig.getKafkaProperties();
        kafkaStreams = new KafkaStreams(aggregator.buildProcessFlow(), props);
       
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                if (!shutdown) {
                    logger.info("Starting Kafka Streams pipeline");
                    kafkaStreams.start();
                }
            }
        });
        return kafkaStreams;
      }
}