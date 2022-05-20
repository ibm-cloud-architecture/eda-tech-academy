# Lab 2 solution


### Developing the Topology in test class

Continuing test with the TopolofyTestDriver, you will implement the topology with the same structure as before, in the class [TestStoreAggregation.java](). Here what the topology needs to do

* 

### The full application code analysis

* The code organization uses the `onion` architecture introduced in the Domain-driven design.

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

The topology is in the Domain layer in the StoreInventoryAggregator class. 

