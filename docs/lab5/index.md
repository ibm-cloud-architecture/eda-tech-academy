# Intro to Instana Observability for Event Streams (Kafka) Messaging

This is a brief introduction to how you can observe your Event Streams Deployment along with applications in a simulated "Day 2" operations mode.

In this exercise, you will see how Instana can monitor: OCP, Kafka Brokers, Zookeeper, Kafka Connect and custom deployed applications.

## Demonstration by Instana Tech Sellers

The Instana Tech Sellers will show a demo of Instana.


## Access the Instana Instance connected to the Event Streams Cluster

1. Go directly to [Instana Home Page](https://training-kafka.instana.io/)   

1. Login using user id: trainerintegration@gmail.com and the password given to you by the instructors.<br>  Note: all attendees will use the same login credentials. <br>After you login you should see something like the following.

    ![](./images/instana-home-page.png)

1. Optional - If this is the first time to the site you might have to dismiss the message in the top right corner by single clicking on it.

    ![](./images/dismiss-deployment-message.png)

1. Explore the left navigation bar by hovering your mouse over the black bar on the left side.  You should see something like the following.

    ![](./images/left-nav-menu-hover.png)


## Base Infrastructure Observability


### OCP Worker Nodes - Cluster Map

1. Go directly to [Infrastructure for OCP Worker Nodes](https://training-kafka.instana.io/#/physical?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1&snapshotId=QllSytQa5I513X22IH_cZ51nTDo) you should see something like the following.

    ![](./images/map-with-zone.png)

1. Click the down arrow next to "Hosts (6)" in the white box in the upper left corner to see all 6 worker nodes.

    ![](./images/ocp-worker-nodes-map.png)

1. Click one of the worker nodes and you should see something like the following.  Note: you can scroll and open accordians.

    ![](./images/ocp-worker-nodes-map-drill-to-worker1.png)

1. Click "Open Dashboard" button and you should see something like the following.

    ![](./images/ocp-worker-nodes-map-drill-to-worker1-dashboard.png)

### OCP Worker Nodes - Cluster k8s Dashboard

1. Go directly to [Infrastructure for OCP Worker Nodes](https://training-kafka.instana.io/#/kubernetes/cluster;clusterId=Q8VhkXFGtFTkz0Wj0ACUwXfbNzw/summary?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.  Note the master and worker nodes are shown along with all the namespaces and the deployments.

    ![](./images/ocp-worker-nodes-k8s-dashboard.png)

### All Kubernetes Services for the Event Streams Cluster

1. Go directly to [Kubernetes Cluster Services for cp4i-eventstreams OCP cluster](https://training-kafka.instana.io/#/kubernetes/namespace;namespaceId=8_1DhmCiu43T_1DQWKwUUAxiObg/services?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.

    ![](./images/base-infra-observability.png)

### Kafka Brokers 

1. Go directly to [Kubernetes Cluster Services for es-demo-kafka-brokers Kafka Brokers](https://training-kafka.instana.io/#/kubernetes/service;serviceId=b3b5HqyUadR4BemJgV4Pt6uYKdk/summary?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.

    ![](./images/kafka-brokers-a.png)

    And Scroll down to see the following.

    ![](./images/kafka-brokers-b.png)

### Zookeeper Services

1. Go directly to [Kubernetes Cluster Services for Zookeeper](https://training-kafka.instana.io/#/kubernetes/service;serviceId=RKawrvO0SzoJarUt6wD4BSJNJ9g/summary?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.

    ![](./images/zookeeper-a.png)

    And Scroll down to see the following.

    ![](./images/zookeeper-b.png)


#### Application and Message Dependencies

1. Go directly to [Application Dependencies for the Messages](https://training-kafka.instana.io/#/application;appId=wCa4qi2XSg-oAEB4c9S-zQ/map?timeline.ws=60000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1&snapshotId=ykF2ZP-cO4WvpdS8FlJXHYsDW38) you should see something like the following.

    ![](./images/message-dependencies.png)


## Application Message Flow and End to End Observability

Instana is the **ONLY** tool that provides full end to end Kafka Message Observability.

### All Services with Dependencies - Unfiltered 

1. Go directly to [Map of All Services with Dependencies - Unfiltered](https://training-kafka.instana.io/#/application;appId=-McjQT-9QiGYFBp41mOkZA/map?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.

    ![](./images/unfiltered-service-map.png)

### Message-based Dependencies - Filtered

1. Go directly to [Map of Message-based Dependencies - Filtered](https://training-kafka.instana.io/#/application;appId=wCa4qi2XSg-oAEB4c9S-zQ/map?timeline.ws=300000&timeline.to=1654895400000&timeline.fm=1654895400000&timeline.ar=false&deployment.page=1) you should see something like the following.

    ![](./images/filtered-message-map.png)


1. The instructors can demonstrate more capabilities for end to end messages.

## Failure & Root Cause Analyis

### Simulate Failure

1. This will be a future exercise and/or demo.

### Drill down to Root Cause

1. This will be a future exercise and/or demo.


## Optional/Takeaway - Explore Instana functionality using the Instana Cloud Sandbox

1. Go directly to [Instana Cloud Sandbox Instance](https://www.instana.com/apm-observability-sandbox/) and login using your IBM email address.

1. After you login using your IBM email addess you should see something like the following.

    ![](./images/instana-sandbox-home.png)


1. Click on the "Get Started" button at the bottom left hand corner.  Follow each of the 5 tutorials to get a feel for Instana.

    ![](./images/sandbox-getting-started.png)











