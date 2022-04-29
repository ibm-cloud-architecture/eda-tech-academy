# Lab 1 solution

The system design of the proof of concept should be simple, but not the global solution, and in the presale work we have to pitch a higher view of what the solution may look like so customer's feels confortable about how your proof of concept solution will fit in a bigger solution.

## Global solution view of real-time inventory

The core principle, is that each components responsible of managing some inventory elements will push event about their own inventory update to a central data hub, that will be used to update back ends, ERP, systems but also exposed data so it will be easy to plug and play streaming processing for computing different aggregates.

The following figure is such high level business view.

![](../images/hl-solution.png)


Servers in the stores and warehouses  (*Store Server* and *Warehouse Server* ) are sending sale transactions to a central messaging platform, where streaming components (the green components) are computing the different aggregates and are publishing those aggregation results to other topics. 

This is a classical data streaming pipeline architecture, as presented in IBM EDA reference architecture.

![](./images/hl-arch-ra.png)

IBM MQ and IBM Event Streams are used to support a shared services to support any asynchronous communication between applications.

Sink connectors, based on Kafka Connect framework, may be used to move data to long persistence storage like s3 bucket, datalake, Database,... or to integrate back to Legacy ERP systems.

The Enterprise Network column includes the potential applications the data pipeline is integrated with.

???- "More information"
    * If you want to reuse the diagram the source is at [this url](https://github.ibm.com/boyerje/eda-tech-academy/blob/main/docs/diagrams/hl-solution.drawio)
    * [EDA reference architecture](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/reference-architecture/#event-driven-architecture)
    * [Kappa architecture](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/reference-architecture/#kappa-architecture)

## Demos / proof of concept view

You want to demonstrate MQ as a source for message coming from Store. Those messages are sent to Event Streams topic via kafka connector.

You may need to simulate store or warehouse events and you can use simple json files for that and the kafka-console-producer.sh shell you can get from any Kafka deployment. 

You will deploy a kafka connector cluster with MQ source connector and different sink connector like elastic search.

![](../images/mq-es-demo.png)