# Event-driven solution - Tech Academy Workshop

The general objective of this academy training is to develop deeper technical skills, within the IBM technical community, to plan, design, build and execute a sub-set of key Proof Of Concept tasks and activities.

In this Event-driven solution workshop, you will learn how to use of Event-Driven/Near Real-time Integration Patterns, Technologies and Implementation Strategies which should help you developing simple Streaming 
applications and proof of concepts. 

## Two possible journeys

We recognize that not every student will have the same level of knowledge of Event Streams and integration capability or even Java development background. We propose two different tracks depending of your current skill level:

1. **Beginner journey**: do the demonstration lab, which is about being confortable to demonstrate Event Streams from "A to Z": this is a good 3 hours run thru exercise that will give you access to a lot of content you should be able to leverage overtime. You may also do the lab 1 as this is a system design mockup exercise, that should help you in the future. [Go to **Track 1** - demonstration lab >>](./demo/)

1. **Advance journey**: Perform labs 1 to 4, this is about learning Kafka Stream, Open Liberty application, using Configuration as Code for OpenShift deployment and GitOps approach. A full story and a set of reusable assets you may want to reuse to build you future proof of concepts. You can also review quickly the demonstration lab, as it could be helpful, as a script to answer specific customer's questions.  [Continue to the goals section below >>](#advance-journey-goals)

## Advance Journey Goals:

* Solidify a full body of knowledge in the near real-time / event driven architecture domain 
* Design and create an initial prototype solution that addresses the DOU goals and objectives.
* Establish an initial point of view for adopting GitOps methods and practices for event streams implementations.
* Give you foundational knowledge to deeply address event-driven solution design 
## Study [Optional] - Lecture: Review key EDA patterns, use cases and usage scenarios.

* **Duration:** 20 minutes.
* **Delivery**: presenter will go over some concepts and presentation of the material. Can be self space study.

What are the technical use cases where Event Streams is a good fit. Why customers are going full speed to adopt EDA?

* [Technical use cases - general positioning](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/usecases/#technical-use-cases)
* [Assessment questions for Event Streams opportunity](https://pages.github.ibm.com/ibm-eda/eda-internal/kafka-assessment/)
* Detailed [use case slides](https://github.ibm.com/ibm-eda/eda-internal/raw/master/docs/eda-usecases/01-EDA-Usecases.pptx)


???- "Read more"
    * [EDA internal site - use cases](https://pages.github.ibm.com/ibm-eda/eda-internal/eda-usecases/)
    * [EDA public web site we use to present content to customers](https://ibm.biz/learn-eda)
## Lab 1: System design for a real-time inventory solution

* **Duration**: 30 minutes
* **Delivery:** Can be done with 2 or 3 students and self pace.

Review the Client provided requirements, and elaborate a system design for an EDA solution.

* [Review the problem statement and the lab's instructions](./lab1/)
* [A potential solution may look like](./lab1/lab1-sol.md)

## Lab 2 [Optional]: Implement a simple item sell or restock event stream processing with Kafka Streams API

Learning the basic of Kafka Streams, and implement a store aggregation processing with Java. The code and environment should be self efficient. 

* **Optional**: if you really hate programming you can skip this lab, or at least read the first few exercices so you can understand some of the streaming concepts.
* **Duration**: 90 minutes
* **Delivery**: Pair programming.

* [Lab's instructions](./lab2/index.md) includes a set of progressing exercise to learn basic Kafka Streams programming.
* [Last exercise solution](./lab2/lab2-sol.md).  
## Lab 3: Deploy the real time inventory solution in one Click

**Duration**: 20 minutes

This is a simple of the end to end solution, you will be able to deploy in few commands. It is also important to review some deployment definition content:

[20 minutes lab](./lab3/index.md)

## Lab 4: Deploy the solution with OpenShift GitOps

**Duration**: 30 minutes

In this lab, you will deploy the ArgoCD applications that monitor your git repository for any change to the configuration
and deploy the different services and MQ broker in your own namespace.

[Lab 4 GitOps deployment](./lab4/index.md)

## Lab 5 [Optional]: [Monitor with Instana](./lab5/index.md)

