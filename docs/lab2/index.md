# Lab 2: Store inventory with Kafka Streams

In this Lab you will do a simple Kafka Streams processing using simple API function to build a streaming topology to compute the store inventory for each items.

The figure illustrates what you need to build, the green rectangle, which is a quarkus application using Kafka Streams API consuming `items` events and compute `store inventory` events by aggregating at the store level.

![](../images/store-inv.png)

In fact most of the code is ready for you for this application, the lab is really to go over simple Streaming implementation and doing so, step by step.

Most of the work will be in the testing classes so you can progress incrementally, and use a very cool API called TopologyTestDriver to test your topology without any Kafka Broker or Java app, just unit tests.
## Kafka Stream concepts

You need to understand some basic Kafka Stream APIs.

### Topology

The business logic is implemented via "topology" that represents a graph of processing operators or nodes. Each node within the graph, processes events from the parent node and may generate events for the down stream node(s). 
This is really close to the Java Streaming APIs or Mutiny APIs, but the APIs used by the topology is from Kafka streams APIS.

Kafka Streams applications are built on top of producer and consumer APIs and are leveraging Kafka capabilities to do data parallelism processing, support distributed coordination of partition to task assignment, and being fault tolerant.

To build a topology we use a Builder class.

```java
final StreamsBuilder builder = new StreamsBuilder();
 //...

final Topology topology = builder.build();
```
### KStreams

### KTable


???- "Read more"
    * [Apache Kafka - TUTORIAL: WRITE A KAFKA STREAMS APPLICATION](https://kafka.apache.org/31/documentation/streams/tutorial)
    * [Kafka Streams summary](https://ibm-cloud-architecture.github.io/refarch-eda/technology/kafka-streams/)
    * [Other labs](https://ibm-cloud-architecture.github.io/refarch-eda/use-cases/kafka-streams/)
## Lab 2 folder

Go to the `/lab2/refarch-eda-store-inventory` folder to do your work, and load this folder into your IDE. Be sure you can run test inside the IDE, it may be easier to debug.


## Test your first topology

We want to print message received from a Kafka topic and filter only events coming from `store_1`.

In your IDE go to the `src/test/java/ut` folder and the class: ``
