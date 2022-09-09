# Workflow Java DSL samples

## Overview 

This is a set of sample workflows illustrating use of Workflow Java DSL of Automatiko.

Take a look at the getting started with Workflow Java DSL [here](https://automatikio.com/component-main/0.0.0/getting-started-code.html)

Samples covered:

- Hello World
- Split of execution based on data conditions
- Split and join of execution paths
- Split based on events - message (from Apache Kafka) or timeout
- Local service invocation - invoke method of a CDI bean from workflow
- REST service invocation - invoke PetStore REST service from workflow
- User interaction with workflow
- Messaging with Apache Kafka (producing and consuming)

## Run it


`docker run -p 8080:8080 automatiko/workflows-java-dsl`

once this is done you can see the fully described service at:

http://localhost:8080/q/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it

There is no special use case implemented here it is more to show Workflow Java DSL in action.