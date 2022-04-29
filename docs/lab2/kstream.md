## Kafka Stream concepts

You need to understand some basic Kafka Stream APIs.

### Topology

The business logic is implemented via "topology" that represents a graph of processing operators or nodes. Each node within the graph, processes events from the parent node and may generate events for the down stream node(s). 
This is really close to the Java Streaming APIs or Mutiny APIs, but the APIs used by the topology is from Kafka streams APIS.

Kafka Streams applications are built on top of producer and consumer APIs and are leveraging Kafka capabilities to do data parallelism processing, support distributed coordination of partition to task assignment, and being fault tolerant.

To build a topology we use a Builder class to define input streams, logic to apply and then produce results

```java
final StreamsBuilder builder = new StreamsBuilder();
 //...

final Topology topology = builder.build();
```
### KStream API

[KStream](https://kafka.apache.org/30/javadoc/org/apache/kafka/streams/kstream/KStream.html) is an abstraction of a Kafka record stream. It can be defined from one ot multiple Topics.

The following declaration is for consuming from topic named `items` with Key and Value of type `String`:

```java
 KStream<String, String> aStream = builder.stream("items",Consumed.with(Serdes.String(), Serdes.String()));
```

Then Kstream offers a lot of functions to process the records. Below is a quick summary of the methd you need

| Method | What it does | Example |
| --- | --- | --- | 
| **peek** | Perform an action on each record of KStream. | aStream.peek((key, value) -> System.out.println(value) |
| **to** | transform the stream to a topic | aStream.to(outTopicName) |
| **filter** | Create a new KStream with records which satisfy the given predicate. |  .filter((key, value) -> ("BLUE".equalsIgnoreCase(value))) |
| **split** | Split this stream into different branches. | aStream.split().branch((key, value) -> value.userId == null, Branched.as("no-userid")).defaultBranch(Branched.as("non-null"));|
| **groupBy** | Group the records of this KStream on a new key  | |

### KTable

KTable is an abstraction of a changelog stream from a primary-keyed table.

A stream can be considered a changelog of a table, where each data record in the stream captures a state change of the table.

| Method | What it does | Example |
| --- | --- | --- | 
| **filter** | Create a new KTable that consists of all records of this KTable which satisfy the given predicate| |
| **join** | | | 

???- "Read more"
    * [Apache Kafka - TUTORIAL: WRITE A KAFKA STREAMS APPLICATION](https://kafka.apache.org/31/documentation/streams/tutorial)
    * [KStream API](https://kafka.apache.org/30/javadoc/org/apache/kafka/streams/kstream/KStream.html)
    * [Ktable API](https://kafka.apache.org/30/javadoc/org/apache/kafka/streams/kstream/KTable.html)
    * [Kafka Streams summary](https://ibm-cloud-architecture.github.io/refarch-eda/technology/kafka-streams/)
    * [Other labs](https://ibm-cloud-architecture.github.io/refarch-eda/use-cases/kafka-streams/)

## Getting started with simple Kafka Streaming 

Go to the `/lab2/refarch-eda-store-inventory` folder to do your work, and load this folder into your IDE. Be sure you can run test inside the IDE, it may be easier to debug.

### Test your first topology

We want to print the messages received from a Kafka topic and filter only events coming from `store_1`.

In your IDE go to the `src/test/java/ut` folder and the class: [TestYourFirstTopology.java](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/test/java/ut/TestYourFirstTopology.java). We will review the most important elements of this test:

* The TopologyTestDriver is a class to test a Kafka Streams topology without Kafka
* the TestInputTopic is used to pipe records to topic in TopologyTestDriver. This is used to send test messages
* The `@BeforeAll` annotation on the `setup()` method means that it will be run before any test is executed, while the @AfterAll annotation on the teardown method ensures that it will be run each time after each test execution. This allows us to spin up and tear down all the necessary components after all test. We use this approach as we have one topology under test in this class.
* The buildTopology method utilizes the StreamsBuilder class to construct a simple topology, reading from the input Kafka topic defined by the inTopicName String. The logic is simple print the message, the filter on a value, and generates the output to the topic:

```java
 KStream<String, String> basicColors = builder.stream(inTopicName,Consumed.with(Serdes.String(), Serdes.String()));
        basicColors.peek((key, value) -> System.out.println("PRE-FILTER: key=" + key + ", value=" + value))
            .filter((key, value) -> ("BLUE".equalsIgnoreCase(value)))
            .peek((key, value) -> System.out.println("POST-FILTER: key=" + key + ", value=" + value))
            .to(outTopicName);
```

* Run this test you should see the following output, with the print of the defined topology and then the execution of the topo on the different input records.

```sh
Topologies:
   Sub-topology: 0
    Source: KSTREAM-SOURCE-0000000000 (topics: [my-input-topic])
      --> KSTREAM-PEEK-0000000001
    Processor: KSTREAM-PEEK-0000000001 (stores: [])
      --> KSTREAM-FILTER-0000000002
      <-- KSTREAM-SOURCE-0000000000
    Processor: KSTREAM-FILTER-0000000002 (stores: [])
      --> KSTREAM-PEEK-0000000003
      <-- KSTREAM-PEEK-0000000001
    Processor: KSTREAM-PEEK-0000000003 (stores: [])
      --> KSTREAM-SINK-0000000004
      <-- KSTREAM-FILTER-0000000002
    Sink: KSTREAM-SINK-0000000004 (topic: my-output-topic)
      <-- KSTREAM-PEEK-0000000003

[or.ap.ka.st.pr.in.StreamTask] (main) stream-thread [main] task [0_0] Initialized
[or.ap.ka.st.pr.in.StreamTask] (main) stream-thread [main] task [0_0] Restored and ready to run
PRE-FILTER: key=C01, value=blue
POST-FILTER: key=C01, value=blue
PRE-FILTER: key=C02, value=red
PRE-FILTER: key=C03, value=green
PRE-FILTER: key=C04, value=Blue
POST-FILTER: key=C04, value=Blue
PRE-FILTER: key=C01, value=blue
POST-FILTER: key=C01, value=blue
[or.ap.ka.st.pr.in.StreamTask] (main) stream-thread [main] task [0_0] Suspended running
[or.ap.ka.st.pr.in.RecordCollectorImpl] (main) topology-test-driver Closing record collector clean
[or.ap.ka.st.pr.in.StreamTask] (main) stream-thread [main] task [0_0] Closed clean
```

### Filter item transaction without a store

**Problem:** 

Try to do the following topology taking the business class [ItemTransaction](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/domain/ItemTransaction.java) as content from the input topic and select only transaction with store and item fields populated.

You can take the [TestSecondTopology test class](), and implement the topology.

The interest here is to use other serialization and real java beans. The Serialization and Deserialization are defined i [StoreSerdes.class](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/infra/events/StoreSerdes.java) which used a [JSON generic class](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/main/java/ibm/gse/eda/stores/infra/events/JSONSerde.java) based on Jackson parser.

* Think to build a Kstream from input stream
* The record should have key and value
* use filter and predicate to test if value.storeName has no value or value.sku is empty or null then drop the message
* generate output to a topic
* Think to chain the functions to get accurate result

Use Test Driven Development to build tests before the topology. Tests are already defined.

???- "Solution"
    The topology looks like
    ```java
    KStream<String,ItemTransaction> items = builder.stream(inTopicName, 
                Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde()));  
       items.filter((k,v) -> 
           (v.storeName != null && ! v.storeName.isEmpty() && v.sku != null && ! v.sku.isEmpty()) 
       )
    .to(outTopicName);
    ```

### Dead letter topic

Extend the topology to route the records in error to a dead letter topic. 

