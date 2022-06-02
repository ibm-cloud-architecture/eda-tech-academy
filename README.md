# [Event-driven architecture - Tech Academy Workshop](https://ibm-cloud-architecture.github.io/eda-tech-academy)

## Goals

The goals of this tutorial is to help tech sellers getting good knowledge of Event-driven solution based on IBM Event Streams, so they could develop quick proof of concepts around event streams solution. 

The tutorial addresses the current overview of the knowledge to have and also reference other materials so you can learn more over time. Some material are still under improvement, all are open sourced so you can contribute too. 

The overall tutorial should be around 4 hours.

## Pre-requisites:

* Get a git client, [docker desktop](https://www.docker.com/products/docker-desktop/) or [podman](https://podman.io/) on developer local laptop
* Get a Java development IDE, we use [Visual Code](https://code.visualstudio.com/)
* OCP access with CP4I installed, we are using [CoC environment](https://cmc.coc-ibm.com/cluster/biggs) as a based for our deployments.


You can read the tutorial content using the [BOOK VIEW](https://ibm-cloud-architecture.github.com/eda-tech-academy)


## Clone this git repository

```sh
git clone  https://github.com/ibm-cloud-architecture/eda-tech-academy.git
```

## Notes for contributors

### Building this booklet locally

The content of this repository is written with markdown files, packaged with [MkDocs](https://www.mkdocs.org/) and can be built into a book-readable format by MkDocs build processes.

1. Install MkDocs locally following the [official documentation instructions](https://www.mkdocs.org/#installation).
1. Install Material plugin for mkdocs:  `pip install mkdocs-material` 
1. `mkdocs serve`
1. Go to `http://127.0.0.1:8000/` in your browser.

### Building this booklet locally but with docker

In some cases you might not want to alter your Python setup and rather go with a docker image instead. This requires docker is running locally on your computer though.

* docker run --rm -it -p 8000:8000 -v ${PWD}:/docs squidfunk/mkdocs-material
* Go to http://127.0.0.1:8000/ in your browser.

### Pushing the book to GitHub Pages

1. Run `mkdocs gh-deploy` from the root directory.

