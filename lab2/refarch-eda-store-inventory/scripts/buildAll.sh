#!/bin/bash
scriptDir=$(dirname $0)

IMAGE_NAME=quay.io/ibmcase/store-aggregator

if [[ $# -eq 1 ]]
then
  TAG=$1
else
  TAG=latest
fi

./mvn clean package -DskipTests
docker build -t  ${IMAGE_NAME}:${TAG} .
docker tag  ${IMAGE_NAME}:${TAG}   ${IMAGE_NAME}:latest
docker push ${IMAGE_NAME}:${TAG}
docker push ${IMAGE_NAME}:latest