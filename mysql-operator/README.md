# Kubernetes Operator example

## Overview 

this is an example showing Kubernetes Operator use cases backed by workflow. It uses Automatiko project to build self described service.

See complete description of this example [here](https://automatikio.com/component-main/0.3.0/examples/operator.html)

## Run it

To be able to run it a Kubernetes cluster must be available. It could be local installation, such as 

- Minikube
- Kind

or it can be cloud based Kubernetes cluster like

- Google Kubernetes Engine
- OpenShift
- Azure kubernetes Engine
- others

### Deploy CRD

Once the Kubernetes cluster is available, let's define our custom resource definition to the Kubernetes Cluster is aware of it.

Download the k8s descriptors from the project source code that can be found [here](https://github.com/automatiko-io/automatiko-examples/tree/main/mysql-operator/k8s).

`kubectl apply -f k8s/crd.yaml`


### Deploy MySQL data base

Since the operator is responsible for provisioning MySQL schemas then there is a need to have MySQL data base
server where these schemas will be created. For that we simply deploy the MySQL container into the Kubernetes cluster.

`kubectl apply -f k8s/mysql-db.yaml`

### Deploy the operator

Once the custom resource definition is deployed to the cluster, the operator itself can be deployed. 
There are several things that needs to be done to the Kubernetes cluster to make the operator
fully operational.

- Deploy the operator container
- Create service account for the operator
- Create cluster role
- Create cluster role binding


`kubectl apply -f k8s/operator.yaml`

Depending on the Kubernetes cluster used there might be a need for extra object to be created to expose the
operator web interface to the consumers outside of the Kubernetes cluster. Following is an example for Minikube

First expose the deployment via load balancer

`kubectl expose deployment mysql-schema-operator --type=LoadBalancer --port=8080 -n mysql-schema`

And then open the service via minikube

`minikube service  mysql-schema-operator  -n mysql-schema` 

once this is done you can see the fully described service at
 [http://localhost:PORT/swagger-ui](http://localhost:PORT/swagger-ui/#)

TIP: You can open your browser [http://localhost:PORT/management/processes/ui](http://localhost:PORT/management/processes/ui) to visualize your running service

### Create new MySQL Schema custom resource

Create new MySQLSchema custom resource in `mysql-schema` namespace

`kubectl apply -f k8s/schema.yaml -n mysql-schema`

This will directly trigger provisioning actual MySQL schema in the data base. In addition to that
it will also create a secret in the same namespace with name the same as custom resource - `mydb-test`.

At the end it will update the custom resource's status with following information

- status - `CREATED`
- url - url to the provisioned schema
- username - user name to be used to connect to the schema
- secret - name of the secret that holds the password for the schema


At this point the schema is successfully provisioned and ready for use. When no loner needed it can be easily removed
when the custom resource is removed from the cluster.

The operator keeps the workflow instance active as long as the associated custom resource is deployed to the
Kubernetes cluster. You can look at the details of each instance by looking at the process management UI component
exposed by the operator


Lastly you can clean up the custom resource by simply removing it from the cluster

`kubectl delete -f k8s/schema.yaml -n mysql-schema`


This will trigger operator that will remove both db schema and user.