# Omboarding example

## Overview 

this is an example showing workflow as a function flow use case. It implements common scenario for serverless usage where 
individual functions builds up a complete business case. In this example it is a simple user registration
that performs various checks and registers users in the Swagger PetStore service.

Upon successful registration of the user, workflow will delegate to another service/function to perform background checks.
It will wait for result of background checks that might be either ok or failed and based on that information 
workflow will continue via different paths.

This examples illustrates how a workflow is sliced into functions that are composed into a function flow based on
the actual logic steered by the workflow definition. Yet each function can be invoked at anytime making the workflow to
act as a definition that can start at any place and continue according to defined flow of activities eka functions.

This example consists of two services that should be deployed

- registration - this is the workflow as function flow service
- notifications - this is a function only service that includes both notifications and background checks functions

## Run it

Workflow as a function flow takes advantage of Knative Eventing as the backbone of the communication to enable

- scalability as each function is invoked via KNative broker
- all data exchange is done with Cloud Events format
- flexibility at which point in the workflow definition instance should be started

So to be able to fully see this example in action a Kubernetes cluster with KNative installed is required. You can follow the
official [Knative documentation about installation steps](https://knative.dev/docs/install/)

Once the Knative and Kubernates cluster is available you can deploy the example by invoking following commands

```
kubectl apply -f notifications/k8s/notifications.yaml
kubectl apply -f registration/k8s/registration.yaml
```

This will provision complete service and all the Knative Eventing triggers


Get the url of the default broker use following command

```
kubectl get broker default
```


Login to a curler pod that enables an easy access to the broker to send requests as it might not be exposed to 
external traffic (e.g. ingress). If your Knative broker is exposed to external traffic you can skip the curler step.

```
kubectl exec -it curler -- /bin/bash
```

Send request to the broker to start user registration for user

```
curl -v "http://broker-ingress.knative-eventing.svc.cluster.local/knativetutorial/default" \
-X POST \
-H "Ce-Id: 1234" \
-H "Ce-Specversion: 1.0" \
-H "Ce-Type: io.automatiko.examples.userRegistration" \
-H "Ce-Source: curl" \
-H "Content-Type: application/json" \
-d '{"user" : {"email" : "mike.strong@email.com",  "firstName" : "mike",  "lastName" : "strong"}}'
```

This will go via number all events being exchanged over the Knative broker and invoking functions defined in the workflow, workflow will 
then invoke the other service's functions to do the background check and emulate notifications.

