# Vacation requests example

## Overview 

this is an example showing human centric backed by data base workflow use case. It implements common scenario for workflows -
emplyee requesting vacations. It takes advantage of workflow features such as

- isolation - each emplyee has his/her dedicated instance secured
- tracking - all vacation requests submitted are tracked
- user interactions - tasks are assigned to individuals to take actions when needed

See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/vacations.html)

## Run it


`docker run -p 8080:8080 automatiko/vacation-requests`

once this is done you can see the fully described service at:

http://localhost:8080/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it

### Authorization

This service is fully secured and requires authentication to be able to interact
with it. Below table represents available users in the service

|User name		|Password			|Roles|
|john@email.com	|john	|employee|
|mary@email.com	|mary	|employee|
|mark@email.com	|mark	|employee|
|joe@email.com	|joe		|employee|
|admin@email.com|admin	|admin|

NOTE: There is also instance level security that is based on `participants` access
policy. This means that given instance will be visible to the owner of it
(the employee) and the assignees of currently active user tasks - for example
manager who has at least one approval task awaiting.

TIP: Note that there is simple UI for manager to see incoming approval tasks.
Just open browser at link:http://localhost:8080[] and connect using `john@email.com`



#### Onboard employee

- Http Method: `POST`
- Endpoint: `http://localhost:8080/vacations`
- Payload
````
{
  "employee": {
    "email": "mary@email.com",
    "firstName": "Mary",
    "lastName": "Jane",
    "startedAt": "2000-12-01",
    "department": "finance"
  }
}
````

complete curl command for this request is as follows

````
curl -u mary@email.com:mary -X POST "http://localhost:8080/vacations" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"employee\":{\"email\":\"mary@email.com\",\"firstName\":\"Mary\",\"lastName\":\"Jane\",\"startedAt\":\"2020-12-26\",\"department\":\"finance\"}}"
````

This will create new instance identified as `mary@email.com`.

#### Submit a vacation request

- Http Method: `POST`
- Endpoint: `http://localhost:8080/vacations/mary@email.com/submit`
- Payload

````
{
  "from": "2020-10-01",
  "length": 15,
  "to": "2020-10-15",
  "key": "vacation"
}
````

complete curl command for this request is as follows

````
curl -u mary@email.com:mary -X POST "http://localhost:8080/vacations/mary%40email.com/submit" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"from\":\"2020-10-01\",\"length\":15,\"to\":\"2020-10-15\",\"key\":\"vacation\"}"
````

#### Fetch approval tasks as manager

- Http Method: `GET`
- Endpoint: `http://localhost:8080/vacations/mary@email.com/request/vacation/tasks`


complete curl command for this request is as follows

````
curl -u john@email.com:john -X GET "http://localhost:8080/vacations/mary%40email.com/requests/vacation/tasks" -H  "accept: application/json"
````

NOTE: Make note of the id returned from the above request as it will be used to complete
the approval task


#### Approve vacation request

- Http Method: `POST`
- Endpoint: `http://localhost:8080/vacations/mary@email.com/requests/vacation/approval/{ID}`
- Payload

````
{
  "approved": true
}
````

IMPORTANT: Replace the `{ID}` at the end of endpoint with ID from the previous call.
Same is required in the below curl command.

complete curl command for this request is as follows

````
curl -u john@email.com:john -X POST "http://localhost:8080/vacations/mary@email.com/requests/vacation/approval/{ID}" -H  "accept: application/json" -H  "Content-Type: application/json" -d "{\"approved\":true}"
````

Now the workflow instance will wait until the first day of vacation request or
until it is cancelled by the employee.

#### Verify access policy restricting access to instances

Each employee has his/her instance completely secured. That means only that
employee can see it or manager - when it has approval task assigned. To verify
this working try to access mary's instance by joe.


- Http Method: `GET`
- Endpoint: `http://localhost:8080/vacations/mary@email.com`


complete curl command for this request is as follows

````
curl -u joe@email.com:joe -X GET "http://localhost:8080/vacations/mary%40email.com" -H  "accept: application/json"
````


#### Clean the state

This is completely for test purpose to allow to use same data for other paths

- Http Method: `DELETE`
- Endpoint: `http://localhost:8080/vacations/mary@email.com`


complete curl command for this request is as follows

````
curl -u mary@email.com:mary -X DELETE "http://localhost:8080/vacations/mary%40email.com" -H  "accept: application/json"
````

