---
title: Real-time inventory solution design
icon: material/emoticon-happy
template: main.html
---

# Real-time inventory solution design

This use case is coming from four customer's engagements since the last three years. 

**Duration**: 30 minutes

## Problem statement

Today, a lot of companies which are managing item / product inventory are facing real challenges to get a close to real-time view of item availability and global inventory view. The solution can be very complex to implement while integrating Enterprise Resource Planning products and other custom legacy systems.

Acme customer is asking you to present how to address this problem using an event-driven architecture 
and cloud native microservices. 

Normally to do MVP, we propose to use an [Event Storming workshop](https://ibm-cloud-architecture.github.io/refarch-eda/methodology/event-storming/) to discover the problem statment, the stakeholders, the business goals, and the business process from an event point of view.  The image below is an example of the outcomes of such event storming session.  

![](../images/es-storming.png)

A MVP will be complex to implement, but the customer wants first to select a technology, so a proof of concept needs to be done to guide them on how our product portfolio will address their problems.

How to start?
## Exercise 1: system design

Giving the problem statement:

**How to design a near real-time inventory view of Stores, warehouses, scaling at millions of transactions per minutes?**

how do you design a solution, that can get visibility of the transactions, at scale, and give a near-real time consistent view of the items in store, cross stores and what do you propose to the customer as a proof of concept.

Start from a white page, design components that you think will be relevant.

**Duration**: 30 minutes

**Expected outcome**

* A diagram illustrating the design. If you use Visual Code you can get the [drawio plugin](https://marketplace.visualstudio.com/items?itemName=hediet.vscode-drawio)
* A list of questions you would like to see addressed

### Some information you gathered from your framing meeting

* SAP is used to be the final system of record
* Company has multiple stores and warehouses
* Sale and restock transactions are in a TLOG format
* Stores and warehouses have local view of their own inventory on local servers and cash machines. 
* Some inventory for warehouses and transactions are done in mainframe and shared with IBM MQ
* Item has code bar and SKU so people moving stock internally can scan item so local system may have visibility of where items are after some small latency.
* The volume of transactions is around 5 millions a day
* Deployment will be between 3 data centers to cover the territory the company work in.
* Customer has heard about kafka, but they use MQ today, CICS, Java EE applications on WebSphere
* Architect wants to deploy to the cloud and adopt flexible microservice architecture
* SAP system will stay in enterprise
* Architect wants to use data lake solution to let data scientists develop statistical and AI  models.


???- "Guidances"
    * Think about enterprise network, cloud providers, stores and warehouses as sources.
    * SAP - Hana is one of the system of record and will get data from it
    * It is also possible to get data before it reaches SAP
    * SAP has database tables that can be used as source of events
    * As warehouse systems are using MQ, queue replication is something to think about


???- "What could be a PoC scope?"
    * You may want to demonstrate Event Streams capabilities as Kafka backbone
    * You want to demonstrate MQ source connector to get message from MQ to Kafka
    * You may want to explain how messages are published from a microservice using Microprofile Reactive Messaging
    * The real-time aggregation to compute store inventory can be done with streaming processing, and Kafka Streams APIs can be used for that.
    * For Data lake integration, you can illustrates messages landing in S3 buckets in IBM Cloud Object Storage using Kafka Sink connector. 


> [See the Solution](./lab1-sol.md)