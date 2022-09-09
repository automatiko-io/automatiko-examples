# Support incidents example

## Overview 

this is an example showing human centric use case but it also takes advantage of data driven decisions within the workflow.

The idea behind this example is to illustrate simplified handling of support incidents. It can be opened by any user and it is automatically classified when it comes to severity and assigned to support team.

At any time reporter and support team can add comments to the case. Comments can also close the case automatically by using keyword `close`. This means that any point in time posting new comment that contains `close` word in it will close the incident completely.

## Run it

This application uses Apache Cassandra a the data store so to run this example you need to have a Cassandra server running and available.

You can run Cassandra as docker container by using this command

````
docker run --name local-cassandra-instance -p 9042:9042 -d cassandra
````

NOTE: You need to point the service to the your cassandra cluster and this can be done by declaring environment variable when starting the service

`docker run -e QUARKUS_CASSANDRA_CONTACT_POINTS=yourserver -p 8080:8080 automatiko/support-incidents`

once this is done you can see the fully described service at:

http://localhost:8080/q/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it


### Start new instance to register incident

- Http Method: `POST`
- Endpoint: `http://localhost:8080/v1_0/incidents`
- Payload

````
{
  "incident": {
    "description": "string",
    "reporter": {
      "company": "string",
      "email": "string",
      "name": "string"
    },
    "severity": "string",
    "title": "string"
  }
}
````

This will start new incident and assigned ID will be your incident number. From now on you can work with support team and reporter
by exchanging comments and optionally reassign to another support team.