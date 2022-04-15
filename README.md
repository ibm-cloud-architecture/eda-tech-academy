# Event-driven architecture - Tech Academy Workshop

## Goal

The goals of this tutorial is to help tech sellers getting good knowledge of Event-driven solution based on IBM Event Streams, so they could develop quick proof of concepts around event streams. 

The tutorial addresses the current overview of the knowledge to have and also reference other material to learn more. Some material are still under improvement, all are open sourced so you can contribute too. 

The overall tutorial should be around 4 hours.

## Pre-requisites:

* Get a git client, docker on developer local laptop
* OCP access with CP4I installed, we are using CoC environment as a based for our yaml and deployment.

## Lecture 1

**Goal:**

* Why EDA is a hot topic
* Business motivation review
* Technical generic use cases.
* Explain what to look for in terms of opportunities and what to propose as PoC.

This will be delivered by presented, over the phone or later with video.

Duration: 45mn

## Lab 1: The IBM Event Streams demonstration

Goal: If not well verse into IBM Event Streams, this lab will help cover all the features from A to Z.

Duration: 30 mn

## Lab 2: System design of a real-time inventory solution.


run .
Lab 2: 30 min
real-time inventory problem statement, data visibility, streaming
ask participants to do a system design on how to address such a problem -
-> Propose the solution for the system design
Lecture 2:

Present EDA reference architecture and streaming architecture
Present fit for purpose for the different technologies involved.
Present the real-time demo
Lab 3:

Deploy manually the solution to go over each steps or use make to deploy in one click.
Delete the deployment
Explain the ES cluster configuration dimensions...

Review event streams listeners and how to connect to the brokers - TLS for encryption between client and broker - mTLS authentication
Review how to use kafka connect cluster with 1 MQ source and 2 sinks (Elasticsearch and cloud object storage)
Review Kafka Streams processing and topology
Review eda-quickstart code template
Review basic Kafka Stream implementation for different problems

## Agenda

## 