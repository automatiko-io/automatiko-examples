# User registration example

## Overview 

this is an example showing workflow as a function use case. It implements common scenario for serverless usage where function builds up a complete business case that is triggered by events coming via HTTP. In this example it is a simple user registration
that performs various checks and registers users in the Swagger PetStore service.


See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/userregistration-function.html)

## Run it

Workflow as a function takes advantage of various serverless environments like

- Azure Functions
- Google Cloud Functions
- Amazon Lambda

Depending on which environment you are targeting there will be different requirements

First select proper profile to build the functon for target environment

- Azure functions use `-Pazure`
- Google Cloud Functions use `-Pgcp`
- Amazon Lambda use `-Paws-lambda`

### Azure functions

Azure configuration uses `azure-functions-maven-plugin` and thus requires additional configuration to be set. 
Either by editing pom.xml file or using `-D` on the command lines. Here are the properties that must be set

- `functionResourceGroup` - resource group in your Azure subscription to be used
- `functionAppPlan` - application plan to be used

Once this is done Azure function deployment can be done via maven plugin

```
mvn clean package azure-functions:deploy -Pazure
```

Completion of the build should end with similar output

```
[INFO] HTTP Trigger Urls:
[INFO] 	 userregistration : https://automatiko-user-registration-example.azurewebsites.net/api/{*path}
```

This is the url that should be used to invoke function, where `{*path}` should be replaced with the function name, in this case `userregistration`

### Google Cloud functions

For Google Cloud functions it should be build with `gcp` profile

```
mvn clean package -Pgcp
```

And then deployed to your Google Cloud project using `gcloud`

```
gcloud functions deploy automatiko-user-registration-example \
  --entry-point=io.quarkus.gcp.functions.http.QuarkusHttpFunction \
  --runtime=java11 --trigger-http --source=target/deployment
```

This command will give you as output a `httpsTrigger.url` that points to your function.


### AWS Lambda

For AWS Lambda functions it should be build with `aws-lambda` profile

```
mvn clean package -Paws-lambda
```

And then deployed to your AWS Lambda project using `sam`

```
sam deploy -t target/sam.jvm.yaml -g
```

This command will give you output similar to following

````
---------------------------------------------------------------------------------------
Outputs
---------------------------------------------------------------------------------------
Key                 UserRegistrationFunctionApi
Description         URL for application
Value               https://xqe0c0addh.execute-api.us-east-2.amazonaws.com/
----------------------------------------------------------------------------------------
````

The above url can be used to trigger lambda functions by appending the context path.




## Use it

Send request to start user registration for user using POST

```
curl -v "FUNCTION_ENDPOINT_URL/api/userregistration" \
-X POST \
-H "Content-Type: application/json" \
-d '{"user" : {"email" : "mike.strong@email.com",  "firstName" : "mike",  "lastName" : "strong"}}'
```

Alternatively you can send request to start user registration for user using GET

```
curl -v "FUNCTION_ENDPOINT_URL/api/userregistration?user.email=john@email.com&user.firstName=John&user.lastName=Strong"
```

Depending on the results it will respond with something like the following response

```
{"id":"3ac3032b-ed91-409c-8faf-7bf3ee9b0482","user":{"id":null,"username":"jstrong","firstName":"John","lastName":"Strong","email":"john@email.com","password":"S3cr3T","phone":null,"userStatus":100}}
```

Important part is the `userStatus` field that represents the output of the function

- 100 - successful registration
- 300 - already registered
- 400 - invalid data
- 500 - server error on registering user in Swagger PetStore