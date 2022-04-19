# Why event-driven architecture

adoption for loosely coupled, event-driven microservice solutions, with new data pipeline used to inject data to modern data lakes, and the adoption of event backbone technology like Apache Kafka, or Apache Pulsar.

Event-driven architecture (EDA) is an architecture pattern that promotes the production, detection, consumption of, and reaction to events. It supports asynchronous communication between components and most of the time a pub/sub programming model. The adoption of microservices brings some interesting challenges like data consistency, contract coupling, and scalability that EDA helps to address.

From the business value point of view, adopting this architecture helps to scale business applications according to workload and supports easy extension by adding new components over time that are ready to produce or consume events that are already present in the overall system. New real-time data streaming applications can be developed which we were not able to do before.

## Technical needs

At the technical level we can see three adoptions of event-driven solutions:

* **Modern data pipeline** to move the classical batch processing of extract, transform and load job to real-time ingestion, where data are continuously visible in a central messaging backbone. The data sources can be databases, queues, or specific producer applications, while the consumers can be applications, streaming flow, long storage bucket, queues, databases…

* **Adopt asynchronous communivation**, publish-subscribe protocol between cloud-native microservices to help to scale and decoupling: the adoption of microservices for developing business applications, has helped to address maintenance and scalability, but pure RESTful or SOAP based solutions have brought integration and coupling challenges that inhibited the agility promised by microservice architecture. Pub/sub helps to improve decoupling, but design good practices are very important.

* **Real time analytics**: this embraces pure analytic computations like aggregate on the data streams but also complex event processing, time window-based reasoning, or AI scoring integration on the data streams.

???- "Read more"
    * [business requirements](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/usecases/)