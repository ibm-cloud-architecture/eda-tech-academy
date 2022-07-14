# Producing & Consuming Data with Event Streams and Schema

## Setting Up The Client Machine

Setting up the sample Kafka Client to be used for the lab.

This section provides the instructions for setting up the Kafka Client that will be used throughout the labs.

1. Check java install

    ![](./images/lab-2-set-1.png)

    ```sh
    C:\Users\rajan>java -version
    At least version 1.8.0_301 should be available.
    ```

	If it’s not installed, download and install the Java Runtime. [https://www.java.com/en/download/manual.jsp](https://www.java.com/en/download/manual.jsp)

2.	Download the sample Kafka Client [from here:](https://github.com/ibm-cloud-architecture/eda-tech-academy/blob/main/tech-jam/KafkaClient_20220131.zip)

3.	Unzip the downloaded Kafka Client (KafkaClient_YYYYMMDD.zip) into a folder called 

    ```sh
    C:\TechJam\EventStreams_Lab\
    ```

4.	Test the client: Open a Command Prompt.

    ```sh
    cd C:\TechJam\EventStreams_Lab\KafkaClient_YYYYMMDD\
	java -jar KafkaClient.jar
    ```

    ![](./images/lab-2-set-2.png)

## Introduction

Version control can be a nightmare for organizations. With Kafka, it’s no different. With stream processing pipelines, there are no files to act as containers for messages with a single format. Let take a look at how Event Streams handles Schema Management with the Schema Registry.

## Lab Objective

In this lab, we’ll do the following: 

* Create a topic and attach a schema to it
* Create a Kafka user with appropriate rights to produce and consume data
* Gather information needed to connect to the Kafka / Schema clusters.
* Test producing / consuming data.
* Make changes to the Schema and see the impact to producer/consumer.

## Pre-Requisites

* Have setup the client machine properly. 
* Able to access the Event Streams web interface. 

## Understanding Schema Registry

### What is a Schema Registry?

Schema Registry provides a serving layer for your metadata. It provides a RESTful interface for storing and retrieving your Avro®, JSON Schema, and Protobuf schemas. 

* It stores a versioned history of all schemas based on a specified subject name strategy, provides multiple compatibility settings.
* Allows evolution of schemas according to the configured compatibility settings and expanded support for these schema types. 
* Provides serializers that plug into Apache Kafka® clients that handle schema storage and retrieval for Kafka messages that are sent in any of the supported formats.

In Event Streams, Schemas are stored in internal Kafka topics by the Apicurio Registry, an open-source schema registry. In addition to storing a versioned history of schemas, Apicurio Registry provides an interface for retrieving them. Each Event Streams cluster has its own instance of Apicurio Registry providing schema registry functionality.

 
![](./images/lab-2-sc-1.png)


### How the Schema Registry Works?

Now, let’s take a look at how the Schema Registry works.

1.	Sending applications request schema from the Schema Registry.
2.	The scheme is used to automatically validates and serializes be for the data is sent.
3.	Data is sent, serializing makes transmission more efficient. 
4.	The receiving application receives the serialized data.
5.	Receiving application request the schema from the Schema Registry. 
6.	Receiving application deserializes the same data automatically as it receives the message.  

![](./images/lab-2-sc-2.png)