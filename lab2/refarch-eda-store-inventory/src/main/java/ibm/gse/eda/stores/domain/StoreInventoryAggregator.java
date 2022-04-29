package ibm.gse.eda.stores.domain;

import javax.inject.Singleton;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.gse.eda.stores.infra.events.StoreSerdes;

/**
 * Stream processing to compute the store inventory for all items
 */
@Singleton
public class StoreInventoryAggregator {
     // Kafka store construct to keep item stocks per store-id
     public static String STORE_INVENTORY_KAFKA_STORE_NAME = "StoreInventoryStock";

    // Input stream is item transaction from the store machines
    @ConfigProperty(name="app.items.topic", defaultValue = "items")
    public String itemSoldInputStreamName;
    // output to store inventory
    @ConfigProperty(name="app.store.inventory.topic", defaultValue = "store.inventory")
    public String storeInventoryOutputStreamName= "store.inventory";


    public StoreInventoryAggregator(){}

    /**
     * The topology processes the items stream into two different paths: one
     * to compute the sum of items sold per item-id, the other to compute
     * the inventory per store. An app can have one topology.
     **/  
    public Topology buildProcessFlow(){
        final StreamsBuilder builder = new StreamsBuilder();
        
        /**
         * implement HERE your topology
         */
       
        KStream<String,ItemTransaction> items = builder.stream(itemSoldInputStreamName, 
        Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde()));     
        // process items and aggregate at the store level 
        KTable<String,StoreInventory> storeItemInventory = items
            // use store name as key, which is what the item event is also using
            .groupByKey(Grouped.with(Serdes.String(), StoreSerdes.ItemTransactionSerde()))
            // update the current stock for this <store,item> pair
            // change the value type
            .aggregate(
                () ->  new StoreInventory(), // initializer when there was no store in the table
                (store , newItem, existingStoreInventory) 
                    -> existingStoreInventory.updateStockQuantity(store,newItem), 
                    materializeAsStoreInventoryKafkaStore());       
        produceStoreInventoryToInventoryOutputStream(storeItemInventory);
        return builder.build();
    }

    private static Materialized<String, StoreInventory, KeyValueStore<Bytes, byte[]>> materializeAsStoreInventoryKafkaStore() {
        return Materialized.<String, StoreInventory, KeyValueStore<Bytes, byte[]>>as(STORE_INVENTORY_KAFKA_STORE_NAME)
                .withKeySerde(Serdes.String()).withValueSerde( StoreSerdes.StoreInventorySerde());
    }

    public void produceStoreInventoryToInventoryOutputStream(KTable<String, StoreInventory> storeInventory) {
        KStream<String, StoreInventory> inventories = storeInventory.toStream();
        inventories.print(Printed.toSysOut());
        inventories.to(storeInventoryOutputStreamName, Produced.with(Serdes.String(), StoreSerdes.StoreInventorySerde()));
    }
    
}
