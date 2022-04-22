# Event-driven architecture - Tech Academy Workshop

The general objective of this academy training is to develop deeper technical skills, within the IBM technical community, to plan, design, build and execute a sub-set of key POC tasks and activities.

In the Event-driven architecture workshop, you will learn how to use of Event-Driven/Near Real-time Integration Patterns, Technologies and Implementation Strategies. 

## Goals:

* Solidified a full body of knowledge in the near real-time / event driven architecture domain 
* Designed and created an initial MVP prototype solution that addresses the DOU goals and objectives.
* Established an initial point of view for adopting GitOps methods and practices for event streams implementations.

## Pre-requisites

* Have a [git client installed](https://github.com/git-guides/install-git)
* Get [docker desktop](https://www.docker.com/products/docker-desktop/) or [podman](https://podman.io/) on developer local laptop
* Get a Java development IDE, we use [Visual Code](https://code.visualstudio.com/)
* OCP access with CP4I installed, could be ROKS, TechZone with CP4I cluster, we are using [CoC environment](https://cmc.coc-ibm.com/cluster/biggs) as a based for our deployments.
## Lecture 1: Review key EDA patterns, use cases and usage scenarios.

What are the technical use cases where Event Streams is a good fit.

* [Technical use cases - general positioning](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/usecases/#technical-use-cases)
* [Assessment questions for Event Streams opportunity](https://pages.github.ibm.com/boyerje/eda-internal/kafka-assessment/)
* Detailed [use case slides](https://github.ibm.com/boyerje/eda-internal/raw/master/docs/eda-usecases/01-EDA-Usecases.pptx)

[Video]()

???- "Read more"
    * [EDA internal site - use cases](https://pages.github.ibm.com/boyerje/eda-internal/eda-usecases/)
## Lab 1: System design for a real-time inventory solution

Review the Client provided requirements, and elaborate a system design for an EDA solution.

[Review the problem statement and the lab's instructions](./lab1/)

???- "Read more"
    * [Demonstration of Event Streams product](https://pages.github.ibm.com/boyerje/eda-internal/demo/demo-script/)
## Lab 2: Implement a simple item sell or restock events with Kafka Streams


[Lab's instructions - 1 hour lab](./lab2)

## Lab 3: Deploy the solution in one Click

This is a simple of the solution using few commands. [10 minutes lab](./lab3)

## Lab 4: Deploy the solution with OpenShift GitOps

In this lab, you will deploy the ArgoCD application that monitor your git repository for any change to the configuration.

[Lab 4 GitOps deployment](./lab4)