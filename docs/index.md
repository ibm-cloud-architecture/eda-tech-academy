# Event-driven solution - Tech Academy Workshop

The general objective of this academy training is to develop deeper technical skills, within the IBM technical community, to plan, design, build and execute a sub-set of key Proof Of Concept tasks and activities.

In this Event-driven solution workshop, you will learn how to use of Event-Driven/Near Real-time Integration Patterns, Technologies and Implementation Strategies which should help you developing simple Streaming 
applications and proof of concepts. 

## Goals:

* Solidify a full body of knowledge in the near real-time / event driven architecture domain 
* Design and create an initial prototype solution that addresses the DOU goals and objectives.
* Establish an initial point of view for adopting GitOps methods and practices for event streams implementations.
* Give you foundational knowledge to deeply address event-driven solution design 
## Pre-requisites

* Have a [git client installed](https://github.com/git-guides/install-git)
* Have a git account into [IBM Internal github](https://github.ibm.com/). 
* Get [docker desktop](https://www.docker.com/products/docker-desktop/) or [podman](https://podman.io/) on your local laptop
* A JDK 11.
* Install the make tool:

    * For Mac: `brew install make`
    * For Windows: See [this note](https://www.technewstoday.com/install-and-use-make-in-windows/)
* Have [oc cli] installed.
* Get a Java development IDE, we use [Visual Code](https://code.visualstudio.com/)
* OCP access with CP4I installed, could be ROKS, TechZone with CP4I cluster, we are using [CoC environment](https://cmc.coc-ibm.com/cluster/biggs) as a base for our deployments.

Fork this repository to your own git account so you can modify content and deploy code from your repository 

![](./lab2/images/fork-repo.png)

and then clone it to your local laptop:

```sh
git clone ...
```
## Study - Lecture: Review key EDA patterns, use cases and usage scenarios.

**Duration:** 20 minutes.

What are the technical use cases where Event Streams is a good fit. Why customers are going full speed to adopt EDA?

* [Technical use cases - general positioning](https://ibm-cloud-architecture.github.io/refarch-eda/introduction/usecases/#technical-use-cases)
* [Assessment questions for Event Streams opportunity](https://pages.github.ibm.com/boyerje/eda-internal/kafka-assessment/)
* Detailed [use case slides](https://github.ibm.com/boyerje/eda-internal/raw/master/docs/eda-usecases/01-EDA-Usecases.pptx)


???- "Read more"
    * [EDA internal site - use cases](https://pages.github.ibm.com/boyerje/eda-internal/eda-usecases/)
    * [EDA public web site we use to present content to customer - ](https://ibm.biz/learn-eda)
## Lab 1: System design for a real-time inventory solution

**Duration**: 30 minutes

Review the Client provided requirements, and elaborate a system design for an EDA solution.

[Review the problem statement and the lab's instructions](./lab1/)

???- "Read more"
    * [Demonstration of Event Streams product](https://pages.github.ibm.com/boyerje/eda-internal/demo/demo-script/)
## Lab 2: Implement a simple item sell or restock event processing with Kafka Streams

Learning the basic of Kafka Streams, and implement a store aggregation processing.

**Duration**: 60 minutes

[Lab's instructions - 1 hour lab](./lab2)

## Lab 3: Deploy the solution in one Click

**Duration**: 20 minutes

This is a sample of the solution using few commands to deploy to OpenShift. [20 minutes lab](./lab3)

## Lab 4: Deploy the solution with OpenShift GitOps

**Duration**: 30 minutes

In this lab, you will deploy the ArgoCD applications that monitor your git repository for any change to the configuration
and deploy the different services and MQ broker in your own namespace.

[Lab 4 GitOps deployment](./lab4)