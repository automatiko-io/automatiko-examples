# User registration example

## Overview 

this is an example showing workflow as a service use case. In this example it is a simple user registration
that performs various checks and registers users in the Swagger PetStore service.

Class that defines the workflow is [UserRegistrationWorkflow.java](src/main/java/io/automatiko/examples/UserRegistrationWorkflow.java).


## Run it

`docker run -p 8080:8080 automatiko/user-registration-javadsl`

once this is done you can see the fully described service at:

http://localhost:8080/q/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui


## Use it

Send request to start user registration for user

```
curl -v "http://localhost:8080/userRegistration" \
-X POST \
-H "Content-Type: application/json" \
-d '{"user" : {"email" : "mike.strong@email.com",  "firstName" : "mike",  "lastName" : "strong"}}'
```
subsequent requests with same payload should result in already registered outcome.

