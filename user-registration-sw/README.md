# User registration example

## Overview 

this is an example showing workflow as a function flow use case. It implements common scenario for serverless usage where 
individual functions builds up a complete business case. In this example it is a simple user registration
that performs various checks and registers users in the Swagger PetStore service.

This examples illustrates how a workflow is sliced into functions that are composed into a function flow based on
the actual logic steered by the workflow definition. Yet each function can be invoked at anytime making the workflow to
act as a definition that can start at any place and continue according to defined flow of activities eka functions.

See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/userregistration.html)

## Run it

Workflow as a function flow takes advantage of KNative Eventing as the backbone of the communication to enable

- scalability as each function is invoked via KNative broker
- all data exchange is done with Cloud Events format
- flexibility at which point in the workflow definition instance should be started

So to be able to fully see this example in action a Kubernetes cluster with KNative installed is required. You can follow the
official [KNative documentation about installation steps](https://knative.dev/docs/install/)

Once the Knative and Kubernates cluster is available you can deploy the example by invoking following commands

```
kubectl apply -f k8s/user-registration.yaml
```

This will provision complete service and all the KNative Eventing triggers

You can also deploy simple event displayer that will print out all events flowing through the broker with following commands

```
kubectl apply -f - << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: event-display
spec:
  replicas: 1
  selector:
    matchLabels: &labels
      app: event-display
  template:
    metadata:
      labels: *labels
    spec:
      containers:
        - name: event-display
          image: gcr.io/knative-releases/knative.dev/eventing/cmd/event_display

---

kind: Service
apiVersion: v1
metadata:
  name: event-display
spec:
  selector:
    app: event-display
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
EOF
```
Deploy the trigger for event displayer that will simply consume all events as there is no filter defined

```
kubectl apply -f - << EOF
apiVersion: eventing.knative.dev/v1
kind: Trigger
metadata:
  name: event-display
spec:
  broker: default
  subscriber:
    ref:
     apiVersion: v1
     kind: Service
     name: event-display
EOF
```

Login to a curler pod that enables an easy access to the broker to send requests as it might not be exposed to 
external traffic (e.g. ingress). If your Knative broker is exposed to external traffic you can skip the curler step.

```
kubectl exec -it curler -- /bin/bash
```

Get the url of the default broker use following command

```
kubectl get broker default
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

This will go via number all events being exchanged over the Knative broker and invoking functions defined in the workflow on workflow.

