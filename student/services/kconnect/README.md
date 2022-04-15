
# Kafka connect configuration for elastic search and cloud object storage sinks and MQ source connectors

## Get Kafka Connector cluster definition to connect to Event Streams

Go to the Event Streams User Interface by using OpenShift credentials. Then select the `Toolbox` menu.

Click on the Set up button for the `Set up a Kafka Connect environment` option and download the zip file. The Zip
includes a `kafka-connect.yaml` to define the Kafka Connect cluster and a Dockerfile to build custom images.

The previous task was already done as we have the `kafka-connect.yaml` file in this folder. The docker image
was pre-built with the needed jars

The following lines were changed to reflect where the container will run and which new image to use.

```yaml
metadata:
  name: es-demi-kconnect-cluster
spec:
  bootstrapServers: es-demo-kafka-bootstrap.cp4i-eventstreams.svc:9093
  image: quay.io/ibmcase/eda-kconnect-cluster-image:latest
```

For maintenance purpose, when new Event Streams product version will be released, the `metadata.annotations` need to be modified to reflect new `productID`,  `cloudpakVersion` and `cloudpakId`.

```
metadata:
        annotations:
          productChargedContainers: eda-kconnect-cluster-connect
          eventstreams.production.type: CloudPakForIntegrationNonProduction
          productID: ....5
          productName: IBM Event Streams for Non Production
          productVersion: 10.5.0
          productMetric: VIRTUAL_PROCESSOR_CORE
          cloudpakId: c8.....
          cloudpakName: IBM Cloud Pak for Integration
          cloudpakVersion: 2021.4.1
          productCloudpakRatio: "2:1"
```


## Deploy the Kafka connect cluster

```sh
oc apply -f kafka-connect.yaml
# Verify cluster is ready
oc get kafkaconnect
```

## Deploy connector

Get the Connect Cluster route

```sh
oc apply -f kafka-connector.yaml
```