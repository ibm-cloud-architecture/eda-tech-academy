# Lab 3: Item inventory demonstration deployment

In this lab, you will learn how to deploy the solution simply using this repository and the minimum set of commands.

Each Student will have received a unique identifier and will modify the current settings in this folder with their student id. 
All the current configurations are currently set for `student_1`, prefix: `std-1`.

We assume the following are pre-set in you OpenShift cluster, which is the same as CoC integration cluster:

* API Connect is installed under `cp4i-apic` project
* Event Streams is installed under `cp4i-eventstreams` project

## pre-requisites

* Have `make` installed. On Mac it should be pre-installed, on Windows [install GnuWin](http://gnuwin32.sourceforge.net/install.html)
* You need the 'oc cli'

## Preparation

As we are using GitOps, you need to have the source of the configuration into your own account.

1. Fork the current repository to your github account: 

    ```sh
    chrome https://github.ibm.com/boyerje/eda-tech-academy
    ```

1. Then clone it to your laptop

    ```sh
    git clone https://github.ibm.com/boyerje/eda-tech-academy.git
    ```
1. Login to your OpenShift cluster

1. Verify the OpenShift GitOps Operator is installed on your OpenShift cluster.

    Work in the `eda-tech-academy/lab3` folder.

    ```sh
    make verify-argocd-available
    ```

    Should get this output if not installed

    ```sh
    Installing
    Complete
    ```

    Or this one if already installed.

    ```sh
    openshift-gitops-operator Installed
    ```

Ready to modify the configurations.


## Modify existing configuration

We will prepare the configuration for the following green components in figure below:

![](./images/student_env.png)

The blue components should have been deployed with the Cloud Pak for Integration deployment. 

*If you are student-1 there is nothing to do, you were lucky...*

1. The demonstration will run on its own namespace. The `env/base` folder includes the definition of the namespace, roles, role binding needed to deploy the demonstration. This is a classical way to isolate apps in kubernetes. 

Running the `updateStudent.sh` shell script, will modify all the yaml files used by the solution with your student id. Two main naming conventions are used: `student-XX` for user name id XX, and `std-XX` prefix. So the namespace for Student-2 will be `sdt-2-rt-inventory` namespace. 

    ```sh
    export USER_NAME=student-2
    export PREFIX=std-2
    export GIT_REPO_NAME=<your-git-user-id>
    ./updateStudent.sh
    ```

1. Start the deployment

```sh
make all-no-gitops
```

1. Verify the solution is up and running

```sh
oc project std-1-rt-inventor
oc get pods
oc get routes 
oc get kafkatopic -n cp4i-eventstreams
oc get kafkauser -n  cp4i-eventstreams
```

1. Execute the demo script

[The instructions are in a separate note](https://ibm-cloud-architecture.github.io/refarch-eda/scenarios/realtime-inventory/#demonstrate-the-real-time-processing)

1. Delete the deployment

If you want to stop working and clean the OpenShift cluster and event streams elements

```sh
make clean-all
```