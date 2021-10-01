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
Just open browser at `http://localhost:8080` and connect using `john@email.com`



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


### Use it with GraphQL interface

Same example can be performed via GraphQL service interface. All the operations can be performed via GraphQL UI
[http://localhost:8080/graphql-ui](http://localhost:8080/graphql-ui)

#### Create new vacation request instance

````
mutation {
  create_vacations(data: {employee: { email: "mary@email.com", firstName: "Mary", lastName: "Jane", startedAt: "2000-12-01T19:20+01:00", department: "finance" }}) {
    id,
    employee {
      department,
      manager
    },
    vacation {
      eligible,
      used
    }
  }
}
````

#### Fetch information about the running instances

````
query {
  get_all_vacations(user:"mary@email.com") {
    id,
    employee {
      firstName,
      manager
    },
    requests {
    	request {
        key,
        from,
        to
      }
    }
  }
}

````


#### Submit one request for days off

````
mutation {
  signal_submit_0(id:"mary@email.com", user:"mary@email.com", model: {key:"xmas", from:"2021-12-24T00:00+01:00", to:"2021-12-28T00:00+01:00", length:4}) {
    requests {
      request {
        key
      }
    },
    vacation {
      eligible,
      used
    }
  }
}

````

##### Get tasks as manager

````
query {
  get_vacations_tasks(id:"mary@email.com", user:"john@email.com") {
    name,
    formLink,
    id
  }
}

````

##### Get tasks as requestor

````
query {
  get_vacations_tasks(id:"mary@email.com", user:"mary@email.com") {
    name,
    formLink,
    id
  }
}

````


#### Approve or reject vacation request 

````
mutation {
  vacations_completeTask_approval_0(parentId:"mary@email.com", id:"xmas", workItemId: "XXXXXXXX", user:"john@email.com", data: {approved:true}) {
    request {
      key,
      approved
    }
  }
}
````

Replace `XXXXXXXX` with actual task id from previous call that fetched tasks for manager

##### Optionally cancel request if no longer needed

````
mutation {
  vacations_completeTask_cancel_1(parentId:"mary@email.com", id:"xmas", workItemId: "XXXXXXXX", user:"mary@email.com", data: {reason:"need to change my plans"}) {
    request {
      key,
      approved,
      cancelled,
      comment
    }
  }
}

````

Replace `XXXXXXXX` with actual task id from previous call that fetched tasks for manager

##### Delete vacation request instance

````
mutation {
  delete_vacations(id:"mary@email.com", user: "mary@email.com") {
    id,
    employee {
      department,
      manager
    },
    vacation {
      eligible,
      used
    }
  }
}
````
