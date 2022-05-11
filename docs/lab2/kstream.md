## Kafka Stream concepts

You need to understand some basic on how to use the Kafka Stream APIs so you can develop some simple streaming applications in a scope of a proof of concepts.

### Topology

The business logic is implemented via a Kafka Streams "topology" which represents a graph of processing operators or nodes. Each node within the graph, processes events from the parent node and may generate events for the down stream node(s). 
This is really close to the Java Streaming APIs or Mutiny APIs, but the APIs used by the topology is from Kafka Streams APIS.

Kafka Streams applications are built on top of Kafka producer and consumer APIs and are leveraging Kafka capabilities to do data parallelism processing, to support distributed coordination of partition to task assignment, and to support fault tolerance.

To build a topology we use a `StreamsBuilder` class to define the input streams (mapped to a Kakfa topic), the logic to apply to the events and then how to produce results to a Kafka topic.

```java
// Create a builder
final StreamsBuilder builder = new StreamsBuilder();
// define the KStream abstraction by defining where the data come from (topic) and in which format
KStream<String,ItemTransaction> items = builder.stream(itemSoldInputStreamName, 
        Consumed.with(Serdes.String(),  StoreSerdes.ItemTransactionSerde())); 
final Topology topology = builder.build();
// start the topology in a thread... we will this code later

```

So let start by playing with KStream construct.

???- "Some Reading"
    If you do not know about topic, kafka producer, and consumer, you may spend time to read some quick [kafka concepts](https://ibm.github.io/event-streams/about/key-concepts/)
    * More [about producer practice](https://ibm.github.io/event-streams/about/producing-messages/)
    * And [consumer](https://ibm.github.io/event-streams/about/consuming-messages/)
### KStream API

[KStream](https://kafka.apache.org/30/javadoc/org/apache/kafka/streams/kstream/KStream.html) is an abstraction of a Kafka record stream. It can be defined from one ot multiple Topics, and will define the structure of the Kafka
record key, and the record structure.

The following declaration is for consuming from topic named `items` with Key and Value of type `String`:

```java
 KStream<String, String> aStream = builder.stream("items",Consumed.with(Serdes.String(), Serdes.String()));
```

Then Kstream offers a lot of functions to process the records. Below is a quick summary of the methods you may need to use in the next exercises:

| Method | What it does | Example |
| --- | --- | --- | 
| **peek** | Perform an action on each record of KStream. | aStream.peek((key, value) -> System.out.println(value) |
| **to** | transform the stream to a topic | aStream.to(outTopicName) |
| **filter** | Create a new KStream with records which satisfy the given predicate. |  .filter((key, value) -> ("BLUE".equalsIgnoreCase(value))) |
| **split** | Split this stream into different branches. | aStream.split().branch((key, value) -> value.userId == null, Branched.as("no-userid")).defaultBranch(Branched.as("non-null"));|
| **groupBy** | Group the records of this KStream on a new key  | |

### KTable

KTable is the second main abstraction of a changelog stream from a primary-keyed table.

A stream can be considered a changelog of a table, where each data record in the stream captures a state change of the table.

The figure below is a simplication of both concepts:

![](./images/stream-table.png)

A Kstream is first connect to a topic and will receive event with Key,Value structure, as unbounded stream. You can chain Kstream to build a topology, and to a Ktable, which will keep only the last value of a given key. To ouput to a Kafka topic, the final construct is a KStream.

KStreams are in memory, Ktables can be persisted in Kafka.

Interesting methods:

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

## Getting started with a simple Kafka Streaming 

Go to the `/lab2/refarch-eda-store-inventory` folder to do your work, and load this folder into your IDE. Be sure you can run tests inside the IDE, it may be easier to debug.

### Exercise 1: Test your first topology

**Duration: 10 minutes**

**Goal:** Test a basic topology inside unit test.

In your IDE go to the `src/test/java/ut` folder and open the class: [TestYourFirstTopology.java](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/lab2/refarch-eda-store-inventory/src/test/java/ut/TestYourFirstTopology.java). The most important elements of this test are:

* The `TopologyTestDriver` is a class to test a Kafka Streams topology without Kafka
* the TestInputTopic is used to pipe test records to a topic in TopologyTestDriver. This is used to send test messages.
* The `@BeforeAll` annotation on the `setup()` method means that it will be run before any test is executed, while the `@AfterAll` annotation on the teardown method ensures that it will be run after last test execution.  We use this approach to define the topology under test in this class.
* The `buildTopology` method utilizes the StreamsBuilder class to construct a simple topology, reading from the input Kafka topic defined by the inTopicName String. The logic is simply to print the message, the filter on a `blue` value, and generates the output to a topic:

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

### Exercise 2: Filter item transaction without a store

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

### Exercise 3: Dead letter topic

Extend the topology to route the records in error to a dead letter topic. This will be using the concept of branches.

