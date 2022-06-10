# Item sold store inventory aggregator component

The goal of this Kafka streams implementation is to build a new real-time 
store inventory view from items sold in different stores of a fictive retailer company. 
The aggregates are kept in state store and exposed via interactive queries.

The goal of this note is to present how to run this store inventory aggregator locally 
using Event Streams Kafka image, and how to build it.

This repository is a smaller copy of [this repository](https://github.com/ibm-cloud-architecture/refarch-eda-store-inventory)
## Pre-requisites

For development purpose the following pre-requisites need to be installed on your working computer:

**Java**
- For the purposes of this lab we suggest Java 11+
- Open Liberty 

**Git client**

**Maven**
- Maven will be needed for bootstrapping our application from the command-line and running
our application.

**Docker**

This project uses the Store Inventory Simulator to produce item sold events to kafka topic.

## Run it with Liberty server in dev and compose.

* Start local Kafka: `docker-compose  up -d` to start one Kafka broker, zookeeper, and the simulator. 
* Verify the `items` and `store.inventory` topics on your Kafka instance
 
 ```shell
./scripts/listTopics.sh 
######################
 List Topics
store.inventory
items
 ```

* Verify each components runs well with `docker ps`:

```sh
CONTAINER ID   IMAGE                                      PORTS                     NAMES
f31e4364dec9   quay.io/ibmcase/eda-store-simulator        0.0.0.0:8082->8080/tcp    storesimulator
2c2959bbda15   obsidiandynamics/kafdrop                   0.0.0.0:9000->9000/tcp    kafdrop
f9d578ffdd91   cp.icr.io/cp/ibm-eventstreams-kafka:11.0.1  0.0.0.0:9092->9092/tcp, 0.0.0.0:29092->9092/tcp   kafka
ee496675c570   cp.icr.io/cp/ibm-eventstreams-kafka:11.0.1   0.0.0.0:2181->2181      zookeeper
```

* Start the app in dev mode: 

```sh
mvn liberty:dev
```

Then [see the demonstration](#demonstration-script) script section below to test the application.

* Build locally

```
mvn package
# or use the script
./scripts/buildAll.sh
```

## Code explanation

The code structure use the 'Onion' architecture. The classes of interest are 

* the `infra.ItemStream` to define the Kafka KStreams from the `items` topic, and the serdes based on `ItemTransaction` event structure.
* the `infra.StoreInventoryStream` to define output stream to `` topic.
* the `domain.ItemProcessingAgent` which goal is to compute the store inventory, which mean the number of items per item id per store

The Kstream logic is simple:

```java
@Produces
    public Topology processItemTransaction(){
        KStream<String,ItemTransaction> items = inItemsAsStream.getItemStreams();     
        // process items and aggregate at the store level 
        KTable<String,StoreInventory> storeItemInventory = items
            // use store name as key, which is what the item event is also using
            .groupByKey(ItemStream.buildGroupDefinitionType())
            // update the current stock for this <store,item> pair
            // change the value type
            .aggregate(
                () ->  new StoreInventory(), // initializer when there was no store in the table
                (store , newItem, existingStoreInventory) 
                    -> existingStoreInventory.updateStockQuantity(store,newItem), 
                    materializeAsStoreInventoryKafkaStore());       
        produceStoreInventoryToInventoryOutputStream(storeItemInventory);
        return inItemsAsStream.run();
    }
```

The functions `materializeAsStoreInventoryKafkaStore` and `produceStoreInventoryToInventoryOutputStream` are classical Kafka stream plumbing code.
Only the above function has business logic.

## Demonstration script

For the up to date demonstration script see [Refarch-eda](https://ibm-cloud-architecture.github.io/refarch-eda/scenarios/realtime-inventory).

### Quick validation for development purpose

For development purpose is a quick demo scripts which can be done in 

```sh
# Once different processes run locally
# If not done yet, use:
./scripts/createTopics.sh
# Verify what is items so far
./scripts/verifyItems.sh
# Trigger the simulator to send few records
curl -X POST http://localhost:8082/api/stores/v1/start -d '{ "backend": "KAFKA","records": 20}'
# Verify store inventory is up to date
curl -X GET "http://localhost:8080/api/v1/stores/inventory/Store_2" -H  "accept: application/json"
##################################
# you should get something like
# {"result":{"stock":{"Item_6":-1,"Item_4":7},"storeName":"Store_2"}}
# Verify store inventory
./scripts/verifyInventory.sh
```

Details:

Once started go to one of the Store Aggregator API: [swagger-ui/](http://localhost:9080/openapi/ui) and select
the `​/api​/v1​/stores​/inventory​/{storeID}` end point. Using the `Store_1` as storeID you should get an empty response.

* Using the user interface at [http://localhost:8082/](http://localhost:8082/)

  ![](./docs/store_simulator.png)

* Use [Kafdrop UI](http://localhost:9000/) to see messages in `items` topic.

  ![](./docs/kafdrop_items.png)

* Verify the store inventory is updated: `curl -X GET "http://localhost:9080/api/v1/stores/inventory/Store_2" -H  "accept: application/json"`
* Verify messages are sent to `store.inventory` topic by 

  ![](./docs/kafdrop_store_inventory.png)

**Remark: after the store aggregator consumes some items, you should see some new topics created, used to persist the 
the stores aggregates.**

