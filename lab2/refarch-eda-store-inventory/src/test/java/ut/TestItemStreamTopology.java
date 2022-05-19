package ut;

import java.util.Properties;

import javax.inject.Inject;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.domain.StoreInventory;
import ibm.gse.eda.stores.domain.StoreInventoryAggregator;
import ibm.gse.eda.stores.infra.events.StoreSerdes;
import io.quarkus.test.junit.QuarkusTest;


/**
 * Use TestDriver to test the Kafka streams topology without kafka brokers
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TestItemStreamTopology {
     
    private  TopologyTestDriver testDriver;

    private  TestInputTopic<String, ItemTransaction> inputTopic;
    private  TestOutputTopic<String, StoreInventory> storeInventoryOutputTopic; 

    @Inject
    private  StoreInventoryAggregator aggregator;
   
    
    public  static Properties getStreamsConfig() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "store-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummmy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,   StoreSerdes.ItemTransactionSerde().getClass());
        return props;
    }


    /**
     * From items streams which includes sell or restock events from a store
     * aggregate per store and keep item, quamntity
     */
    @BeforeEach
    public void setup() { 
        Topology topology = aggregator.buildProcessFlow();
        testDriver = new TopologyTestDriver(topology, getStreamsConfig());
        inputTopic = testDriver.createInputTopic(aggregator.itemSoldInputStreamName, 
                                new StringSerializer(),
                                StoreSerdes.ItemTransactionSerde().serializer());
        storeInventoryOutputTopic = testDriver.createOutputTopic(aggregator.storeInventoryOutputStreamName, 
                                new StringDeserializer(), 
                                StoreSerdes.StoreInventorySerde().deserializer());
    }

    @AfterEach
    public  void tearDown() {
        try {
            testDriver.close();
        } catch (final Exception e) {
             System.out.println("Ignoring exception, test failing due this exception:" + e.getLocalizedMessage());
        } 
    }

    @Test
    @Order(1)  
    public void shouldGetAStoreInventoryWithTwoItems() {
        // given two items are stocked in the same store
        ItemTransaction item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,5,33.2);
        inputTopic.pipeInput(item.storeName, item);
        item = new ItemTransaction("Store-1","Item-2",ItemTransaction.RESTOCK,10,33.2);
        inputTopic.pipeInput(item.storeName, item);
        // the inventory keeps the store stock per items
        ReadOnlyKeyValueStore<String,StoreInventory> inventory = testDriver.getKeyValueStore(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME);
        StoreInventory aStoreStock = (StoreInventory)inventory.get("Store-1");
        Assertions.assertEquals(5L,  aStoreStock.stock.get("Item-1"));
        Assertions.assertEquals(10L,  aStoreStock.stock.get("Item-2"));
    }



    @Test
    @Order(2)  
    public void shouldGetInventoryUpdatedQuantity(){
        //given an item is stocked in a store
        ItemTransaction item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,5,33.2);
        inputTopic.pipeInput(item.storeName, item);
        // and then sold        
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.SALE,2,33.2);
        inputTopic.pipeInput(item.storeName, item);
        // verify an store inventory aggregate events are created with good quantity
        Assertions.assertFalse(storeInventoryOutputTopic.isEmpty()); 
        Assertions.assertEquals(5, storeInventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
        Assertions.assertEquals(3, storeInventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
    }
    
    @Test
    @Order(3)  
    public void shouldGetRestockQuantity(){
        // given an item is stocked in a store
        ItemTransaction item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,5,20);
        inputTopic.pipeInput(item.storeName, item);        
        item = new ItemTransaction("Store-1","Item-1",ItemTransaction.RESTOCK,2,20);
        inputTopic.pipeInput(item.storeName, item);

        Assertions.assertFalse(storeInventoryOutputTopic.isEmpty()); 
        // can validate at the <Key,Value> Store
        ReadOnlyKeyValueStore<String,StoreInventory> storage = testDriver.getKeyValueStore(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME);
        StoreInventory i = (StoreInventory)storage.get("Store-1");
        // the store keeps the last inventory
        Assertions.assertEquals(7L,  i.stock.get("Item-1"));
        // the output streams gots all the events
        Assertions.assertEquals(5, storeInventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
        Assertions.assertEquals(7, storeInventoryOutputTopic.readKeyValue().value.stock.get("Item-1"));
     
     }
 
}