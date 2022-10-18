# Configuring and Running MirrorMaker 2 (MM2)

Rajan Krishnan,  IBM

## Introduction

MirrorMaker2,  released  as  part  of  Kafka  2.4.0,  allows  you  to  mirror  multiple  clusters  and create many replication topologies. Learn all about this awesome new tool and how to reliably and easily mirror clusters.

## Lab Objectives

In  this  lab,  we  will  review  the  basic  concepts  of  MirrorMaker2.  You  will  demonstrate  the ability to migrate a topic from a source Kafka to a destination Kafka platform. You will ensure the migration was successful by checking the main properties of topics and consumers and comparing the source / destination Kafka clusters. 

## Pre-Requisites

* Working instance of Event Streams.
* A source Kafka cluster (e.g. Strimzi).

## Understanding MirrorMaker 2 (MM2)

MM2 addresses some of the problems with legacy MM (MM1).

* Offset translation –avoid duplicates.
* Consumer group checkpoints.
* Topics con figurations synced (e.g. partitions, ACLs).
* MM2 takes advantage of the Kafka Connect ecosystem. 
    
MM2 has 3 main components / connectors.

1. MirrorSourceConnector
        
    * Replicates remote topics, topic configurations.
    * Emits Offset syncs2

1. MirrorCheckPointConnector
        
    * Consumes offset syncs.
    * Combines offset syncs and consumer offset to emit checkpoint
    
1. MirrorHeartBeatConnector
    
    * Used for monitoring replication flows. 
    * Sends source heartbeats to remote. 

![](./images/lab-4-1.png)

As part of the lab, we will deploy the Source Connectorand Checkpoint Connector

## Lab Procedures

In this lab, we will migrate data from an external Kafka topic to a local Kafka topic in Event Stream. The external Kafka can be Strimzi, Confluent, Opensource Kafka, another Event Stream platform or any other Kafka platform. For the purpose of this lab, we will use a Strimzi Kafka platform. 

When the lab steps are complete, the following details should be replicated between the source Kafka and destination Kafka:

* All the messages in the source kafka topic.
* Timestamp of messages should be preserved.
* The consumer groups and the respective lags.
Message offset value will not be preserved due to retention policy in source kafka. 

### 1. Take note of source Kafka topic details. 


You can check the status of strimzi topic here:

[https://kafdrop-strimzi.rajancluster-syd01-992844b4e64c83c3dbd5e7b5e2da5328-0000.au-syd.containers.appdomain.cloud/topic/basicuserdata](https://kafdrop-strimzi.rajancluster-syd01-992844b4e64c83c3dbd5e7b5e2da5328-0000.au-syd.containers.appdomain.cloud/topic/basicuserdata)  

Take note of the topic name, consumer group, consumer lags and last offset message. 

![](./images/lab-4-2.png)

![](./images/lab-4-3.png)


### 2. Take note of your KafkaConnect_URL. 

Use one of the following KafkaConnect_URL depending on the Openshift Cluster that has been assigned to you

| Host | URL |
| --- | --- |
| Ventress | [connect-cp4i-eventstreams.apps.ventress.coc-ibm.com](https://connect-cp4i-eventstreams.apps.ventress.coc-ibm.com) |
| Cody| [connect-cp4i-eventstreams.apps.cody.coc-ibm.com](https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com)  |
| Finn | [connect-cp4i-eventstreams.apps.finn.coc-ibm.com](https://connect-cp4i-eventstreams.apps.finn.coc-ibm.com)  |  

### 3. Kafka Connect

Check if Kafka Connect is already setup in the cluster and the MirrorMaker 2 plugins are available. 

Open a Command Terminal and enter this command. 

```sh
curl -ki -X GET -H "Accept: application/json" https://<KAFKACONNECT_URL>/connector-plugins
```

Example:

```sh
curl -ki -X GET -H "Accept: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connector-plugins/
```

You should get an output similar to this. 

!!! note
    You may use tools like [jq](https://stedolan.github.io/jq/) to format the json output in an easy to read format. 


Ensure the MirrorMaker related plugins are listed. 

```json
[
{
    "class": "org.apache.kafka.connect.file.FileStreamSinkConnector",
    "type": "sink",
    "version": "3.1.1"
},
{
    "class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "type": "source",
    "version": "3.1.0"
},
{
    "class": "org.apache.kafka.connect.mirror.MirrorCheckpointConnector",
    "type": "source",
    "version": "1"
},
{
    "class": "org.apache.kafka.connect.mirror.MirrorHeartbeatConnector",
    "type": "source",
    "version": "1"
},
{
    "class": "org.apache.kafka.connect.mirror.MirrorSourceConnector",
    "type": "source",
    "version": "1"
}
]
```

### 4. MM2 Connector Definitions

1. Download the MM2 connectors JSON file sample that will be used for creating connectors.

    ```sh
    curl -X GET https://raw.githubusercontent.com/ibm-cloud-architecture/eda-tech-academy/main/tech-jam/mm2_json_files/mm2_source.json > mm2_source.json
    curl -X GET https://raw.githubusercontent.com/ibm-cloud-architecture/eda-tech-academy/main/tech-jam/mm2_json_files/mm2_checkpoint.json > mm2_checkpoint.json
    ```

    You will find 2 json files (`mm2_source.json & mm2_checkpoint.json`). These 2 files will be used in the next steps. Copy the files to `C:\TechJam\EventStreams_Lab\` Folder.

1. Create the MM2 source connector JSON file. 

    Edit the sample file `mm2_source.json` in the `C:\TechJam\EventStreams_Lab\` Folder. Fill-in the details accordingly. 

    Fields not mentioned in this table can be left to their default values. 

    | parameter | Value |
    | --- | --- |
    | **name**	| Enter your studentID. Example: student60 |
    | **source.cluster.alias**	| A name that you will give to the source cluster. All replicated topics will have a prefix of this alias.Make sure to use the studentID as the prefix. Example: student60-source |
    | **target.cluster.alias**	| A name that you will give to the target cluster. Make sure to use the studentID as the prefix. Example: student60-target|
    |  **source.cluster.bootstrap.servers**	| Kafka Bootstrap URL of the source Strimzi cluster. You can use this. `rajancluster-syd01-992844b4e64c83c3dbd5e7b5e2da5328-0000.au-syd.containers.appdomain.cloud:31688` |
    | **target.cluster.bootstrap.servers**	| Kafka Bootstrap URL of the target Event Stream Cluster. For the purpose of this lab, we have created a PLAIN connection. `es-demo-kafka-bootstrap.cp4i-eventstreams.svc:9092` |
    | **topics**	| Use the topic called “basicuserdata” that has been created for the purpose of this lab. | 

    ![](./images/lab-4-4.png)

    This is a sample of how the file should look like. 


### 5. MM2 connector deployment

1.	Create a MM2 Source Connector.

    We will use REST API to create the connector. Open a command prompt. Type this command.

    ```sh
    cd C:\TechJam\EventStreams_Lab\

    curl -ki -X POST -H "Content-Type: application/json" https://<KAFKACONNECT_URL>/connectors --data "@./mm2_source.json"

    # Example:

    curl -ki -X POST -H "Content-Type: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors --data "@./mm2_source.json"
    ```

1.	Check the status of the MM2 connector. From the command prompt, type:

    ```sh
    # First Get the connector name from the output. 
    curl -ki -X GET -H "Accept: application/json" https://<KAFKACONNECT_URL>/connectors

    # Example:
    curl -ki -X GET -H "Accept: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors/

    ["cody20"]

    # Then, get the status of the connector.
    curl -ki -X GET -H "Accept: application/json" https://<KAFKACONNECT_URL>/connectors/<connector_name>/status

    # Example:
    curl -ki -X GET -H "Accept: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors/cody20/status/

    {
    "name": "cody20",
    "connector": {
        "state": "RUNNING",
        "worker_id": "10.128.0.52:8083"
    },
    "tasks": [
        {
            "id": 0,
            "state": "RUNNING",
            "worker_id": "10.128.0.52:8083"
        }
    ],
    "type": "source"
    }
    ```

### 6. Verify topic creation

1. Check the EventStreams web console and a new topic should have been created. 
Look for the replicated topic. The topic should be named as: `<SOURCE_CLUSTER_ALIAS>-basicuserdata` Example: cody20-source.basicuserdata

![](./images/lab-4-5.png)

### 7. Create MM2 Checkpoint

1.	Create the MM2 Checkpoint connector JSON file. 
    
    Edit the sample file `mm2_checkpoint.json` in the `C:\TechJam\EventStreams_Lab\` Folder. Fill-in the details accordingly.  Fields not mentioned in this table can be left to it’s default values. 

    | parameter | Value |
    | --- | --- |
    | **name**	| Enter your studentID-checkpoint. Example: cody20-checkpoint |
    | **source.cluster.alias** |	A name that you will give to the source cluster. All replicated topics will have a prefix of this alias.Make sure to use the studentID as the prefix. Example: cody20-source |
    | **target.cluster.alias** |	A name that you will give to the target cluster. Make sure to use the studentID as the prefix. Example: cody20-target |
    | **source.cluster.bootstrap.servers** |	Kafka Bootstrap URL of the source Strimzi cluster. `rajancluster-syd01-992844b4e64c83c3dbd5e7b5e2da5328-0000.au-syd.containers.appdomain.cloud:31688` |
    | **target.cluster.bootstrap.servers** |	Kafka Bootstrap URL of the target Event Stream Cluster. For the purpose of this lab, we have created a PLAIN connection. `es-demo-kafka-bootstrap.cp4i-eventstreams.svc:9092` |
    | **groups** |	The consumer groups that has to be replicated. For the purpose of this lab we will replicate all groups in the source Kafka. So, use the value “.*” |

    ![](./images/lab-4-6.png)

    This is a sample of how the file should look like. 

1. Create a MM2 Checkpoint Connector.

    We will use REST API to create the connector. Open a command prompt. Type this command.

    ```sh
    cd C:\TechJam\EventStreams_Lab\

    curl -ki -X POST -H "Content-Type: application/json" https://<KAFKACONNECT_URL>/connectors --data "@./mm2_checkpoint.json"

    # Example:
    curl -ki -X POST -H "Content-Type: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors --data "@./mm2_checkpoint.json"
    ```

1. Check the status of the MM2 Checkpoint connector. From the command prompt, type:

    First Get the connector name from the output. 

    ```sh
    curl -ki -X GET -H "Accept: application/json" <KAFKACONNECT_URL>/connectors

    # Example:
    curl -ki -X GET -H "Accept: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors/

    ["cody20","cody20-checkpoint"]

    # Then, get the status of the checkpoint connector.
    curl -ki -X GET -H "Accept: application/json" https://<KAFKACONNECT_URL>/<connector_name>/status

    Example:
    curl -ki -X GET -H "Accept: application/json" https://connect-cp4i-eventstreams.apps.cody.coc-ibm.com/connectors/cody20-checkpoint/status/

    {
    "name": "cody20-checkpoint",
    "connector": {
        "state": "RUNNING",
        "worker_id": "10.128.0.52:8083"
    },
    "tasks": [
        {
            "id": 0,
            "state": "RUNNING",
            "worker_id": "10.128.0.52:8083"
        }
    ],
    "type": "source"
    }
    ```

### 8. Check consumer Lag.

Check consumer lags in Event Streams. 

* Go to Event Streams home page -> Topics -> Click on the replicated topic -> Click on “Consumer Groups”.

The new consumer group should appear and the offset lag should match with that of strimzi. 



![](./images/lab-4-7.png)

* Do a random check on the timestamp of a message. 

![](./images/lab-4-8.png)


Now you can consume the data from the Event Streams cluster. You will only be consuming the remaining messages. 

Make a copy of the `config.properties` file.  Call it `config.properties.mm2`
Edit the `config.properties.mm2 `file and change the following fields.   
You will have to create a new SCRAM user, and this time, at the point where you choose consumers, select "All Consumers".   


| Field	| Value |
| --- | --- |
| **sasl.jaas.config** |	Use the newly created SCRAM user and password |
| **enableschemaavro** |	False |
| **topic** | 	The newly replicated topic. Example: student60-source.basicuserdata |
| **group.id** |	You should use the same Consumer Group as the one that has been replicated.     general-strimzi-consumer-v1 |

Run the KafkaClient in consumer mode:

```sh
java -jar KafkaClient.jar consumer config.properties.mm2
```

It should only consume the number of messages as listed in the lags (and not the entire message data in the topic). 
