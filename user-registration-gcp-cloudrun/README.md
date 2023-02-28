# User registration example

## Overview 

this is an example showing workflow as a function flow use case. It implements common scenario for serverless usage where 
individual functions builds up a complete business case. In this example it is a simple user registration
that performs various checks and registers users in the Swagger PetStore service.

This examples illustrates how a workflow is sliced into functions that are composed into a function flow based on
the actual logic steered by the workflow definition. Yet each function can be invoked at anytime making the workflow to
act as a definition that can start at any place and continue according to defined flow of activities eka functions.

See complete description of this example [here](https://docs.automatiko.io/main/0.0.0/examples/userregistration.html)

## Run it

Workflow as a function flow takes advantage of KNative Eventing as the backbone of the communication to enable

- scalability as each function is invoked via KNative broker
- all data exchange is done with Cloud Events format
- flexibility at which point in the workflow definition instance should be started

So to be able to fully see this example in action a Kubernetes cluster with KNative installed is required. You can follow the
official [KNative documentation about installation steps](https://knative.dev/docs/install/)

This project is configured to run on [Google CloudRun](https://cloud.google.com/run) and use Google PubSub/Eventarc to trigger functions.

You need to create a project and enable CloudRun to completely use this example. It's id should be defined in application.properties
file that is located under `src/main/resources` folder

Once you have the project you should:

create following topics

- io.automatiko.examples.userRegistration
- io.automatiko.examples.userRegistration.getuser
- io.automatiko.examples.userRegistration.notregistered
- io.automatiko.examples.userRegistration.generateusernameandpassword
- io.automatiko.examples.userRegistration.registeruser
- io.automatiko.examples.userRegistration.notifyregistered
- io.automatiko.examples.userRegistration.userregistered
- io.automatiko.examples.userRegistration.alreadyregistered
- io.automatiko.examples.userRegistration.invaliddata

when deploying to CloudRun create triggers for each of the topic that is PubSub with messagePublished event.

NOTE: For your convenience Automatiko generates a complete set of scripts for `gcloud` that are ready to be used. 
Look into `target/scripts` folder to find `gcloud scripts` for both deploying and undeploying.   

Once that is done you can publish first message (using Google Console) to the io.automatiko.examples.userRegistration topic
with following content.


```
{"user" : {"email" : "mike.strong@email.com",  "firstName" : "mike",  "lastName" : "strong"}}
```

This will go via number all events being exchanged over the pub sub infrastructure and invoking functions defined in the workflow on workflow.

