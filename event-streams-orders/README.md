# Event stream example

Overview this is an example showing event stream use cases backed by workflow. It uses Automatik project to build self described service that makes use of Apache Kafka as event source.

## Run it

The only thing that is needed is Apache Kafka running that will be accessible. It needs to be given as part of run command via environment variable `KAFKA_SERVERS`

`docker run -e KAFKA_SERVERS=KAFKA_HOST:9092 -p 8080:8080 mswiderski/event-streams-orders`

once this is done you can see the fully described service at:

http://localhost:8080/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it

This sample service utilises both Key and value of the Kafka Record. Key is used to uniquely identify order so it should be sort of an identifier of the order such as `ORDER-XXX` where XXX is sequence number. Value is actual payload that can be

- order information
- order item information

Below is an example that runs through "happy path" to create order, add bunch of items to it then place the order and finally shipped the order. All these actions are done by publishing records to Kafka broker.


###  Create new order

- Topic `orders`
- Set record key to `ORDER-1`
- Set the record value to 

````
{
  "order": {
    "customer": {
      "address": {
        "city": "New York",
        "country": "US",
        "street": "Main Street 1",
        "zipCode": "10000"
      },
      "email": "john@doe.org",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "123456"
    },
    "orderDate": "2020-12-07",
    "orderNumber": "ORDER-1",
    "status": "Created"
  }
}
````

### Add items to the order

- Topic `orders`
- Set record key to `ORDER-1`
- Set record value to

````
{
  "item" : {
    "articleId" : "1234",
    "name" : "pen",
    "price" : 10,
    "quantity" : 4
  }
}
````

You can add as many items as you like, just keep the articleId unique as this is used by the process to manage quantity of the item.
If the quantity is positive number it is added
If the quantity is negative number it is deducted
if the quantity is set to 0 then it is completely removed from the order

### Place the order

- Topic `orders`
- Set record key to `ORDER-1`
- Set record value to

````
{
  "order": {
    "customer": {
      "address": {
        "city": "New York",
        "country": "US",
        "street": "Main Street 1",
        "zipCode": "10000"
      },
      "email": "john@doe.org",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "123456"
    },
    "orderDate": "2020-12-07",
    "orderNumber": "ORDER-1",
    "status": "Placed"
  }
}
````

### Ship the order

- Topic `orders`
- Set record key to `ORDER-1`
- Set record value to

````
{
  "order": {
    "customer": {
      "address": {
        "city": "New York",
        "country": "US",
        "street": "Main Street 1",
        "zipCode": "10000"
      },
      "email": "john@doe.org",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "123456"
    },
    "orderDate": "2020-12-07",
    "orderNumber": "ORDER-1",
    "status": "Shipped"
  }
}
````

This will complete the process of `ORDER-1`

### Additional operations

In addition at any time order can be cancelled by

- Topic `orders`
- Set record key to `ORDER-1`
- Set record value to

````
{
  "order": {
    "customer": {
      "address": {
        "city": "New York",
        "country": "US",
        "street": "Main Street 1",
        "zipCode": "10000"
      },
      "email": "john@doe.org",
      "firstName": "John",
      "lastName": "Doe",
      "phone": "123456"
    },
    "orderDate": "2020-12-07",
    "orderNumber": "ORDER-1",
    "status": "Cancelled"
  }
}
````

Since orders are placed by customers, their address can change so at any time customer address can be changed by publishing another record to customers topic

- Topic `customers`
- Set record key to `john@doe.org` (email address of the customer)
- Set record value to

````
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@doe.org",
    "phone": "123456",
    "address": {
        "street": "Second avenue 4",
        "city": "Boston",
        "zipCode": "00022",
        "country": "US"
    }
}
````


# Build from source

Building from source is just a matter to run maven commands. Though first and foremost is to select what should be built

## Build in development mode

Building in development mode allow to run the application and make modifications to t without restarting it.

`mvn clean quarkus:dev`

## Building executable jar

Building as executable jar allow to run the application is standalone mode.

`mvn clean package`

## Building native image

Building native image allows to build single executable binary file containing everything that application needs, including JDK classes.

`mvn clean package -Pnative`

NOTE: this requires GraalVM installation and it rather heavy operation.

## Building container image (with executable jar)

Building container image allows to build the application once and deploy it to any conainer runtime.

`mvn clean package -Pcontainer`

## Building for Kubernetes

Building for Kubernetes allows easy deployment to Kubernetes environment. It will build container and generate deployment files (json and yaml) for simple deployment to Kubernetes.

`mvn clean package -Pkubernetes`