# Lab 2 solution

## The output inventory class

The class needs to keep store name and a map of items and current inventory. The class is [StoreInventory](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/domain/StoreInventory.java)

```java
public class StoreInventory  {
    
    public String storeName;
    // map <item_id,quantity>
    public HashMap<String,Long> stock = new HashMap<String,Long>();
```

This class is used to get to the out topic but inside the Store and aggregate via the update method:

```java
public StoreInventory updateStockQuantity(String key, ItemTransaction newValue) {
    this.storeName = key;
    if (newValue.type != null && ItemTransaction.SALE.equals(newValue.type))
        newValue.quantity=-newValue.quantity;
    return this.updateStock(newValue.sku,newValue.quantity);
}

public StoreInventory updateStock(String sku, long newV) {
    if (stock.get(sku) == null) {
        stock.put(sku, Long.valueOf(newV));
    } else {
        Long currentValue = stock.get(sku);
        stock.put(sku, Long.valueOf(newV) + currentValue );
    }
    return this;
}
```

## Developing the Topology in test class

Continuing test with the TopolofyTestDriver, you will implement the topology with the same structure as before, in the class [TestStoreAggregation.java](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/test/java/ut/TestStoreAggregation.java). Here what the topology needs to do:

* Get ItemTransaction from input stream the Key being the storeName
* Aggregation wwork on keyed group, so groupByKey the input records
* Aggregate using the update method.

The stream topology looks like:

```java
    KStream<String,ItemTransaction> items = builder.stream(inTopicName, 
            Consumed.with(Serdes.String(),  
            StoreSerdes.ItemTransactionSerde()));  
    // 2 processing   
    // process items and aggregate at the store level 
    KTable<String,StoreInventory> storeItemInventory = items
        // use store name as key, which is what the item event is also using
        .groupByKey()
        // update the current stock for this <store,item> pair
        // change the value type
        .aggregate(
            () ->  new StoreInventory(), // initializer when there was no store in the table
            (store , newItem, existingStoreInventory) 
                -> existingStoreInventory.updateStockQuantity(store,newItem), 
                Materialized.<String, 
                        StoreInventory, 
                        KeyValueStore<Bytes, byte[]>>as(STORE_INVENTORY_KAFKA_STORE_NAME)
                        .withKeySerde(Serdes.String())
                        .withValueSerde( StoreSerdes.StoreInventorySerde())
                );   
    // Generate to output topic
    storeItemInventory.toStream().to(outTopicName,
            Produced.with(Serdes.String(), StoreSerdes.StoreInventorySerde()));

```

## The full application code analysis

In fact the topology creation is defined in a business service. The microservice application is using the Liberty runtime and API and the code organization uses the `onion` architecture introduced in the Domain-driven design:

    * `domain` contains the business logic and business entities related to item transaction and store inventory.
    * `infra` is for infrastructure code, containing JAXRS class, event processing, and ser-des.

```
                ├── app
                │   └── StoreAggregatorApplication.java
                ├── domain
                │   ├── ItemTransaction.java
                │   ├── StoreInventory.java
                │   └── StoreInventoryAggregator.java
                └── infra
                    ├── api
                    │   ├── StoreInventoryQueries.java
                    │   ├── StoreInventoryResource.java
                    │   ├── VersionResource.java
                    │   └── dto
                    │       ├── InventoryQueryResult.java
                    │       ├── ItemCountQueryResult.java
                    │       └── PipelineMetadata.java
                    └── events
                        ├── ItemProcessingAgent.java
                        ├── JSONSerde.java
                        ├── KafkaConfig.java
                        ├── KafkaPropertiesUtil.java
                        └── StoreSerdes.java
```

The topology is in the Domain layer in the [StoreInventoryAggregator class]()https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/domain/StoreInventoryAggregator.java.

The Topology is started in a thread in the [ItemProcessingAgent class](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/infra/events/ItemProcessingAgent.java) when the application starts, by looking at the `StartupEvent`

```java
    void onStart(@Observes StartupEvent ev){
        this.kafkaStreams = initializeKafkaStreams();
		logger.info("ItemProcessingAgent started");
     }
```

