# Internet of Things example

## Overview 

this is an example showing event stream use cases backed by workflow for IoT. It uses Automatik project to build self described service that makes use of MQTT broker as event source.

See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/leaks.html)

## Run it

The only thing that is needed is MQTT broker running that will be accessible. It needs to be given as part of run command via environment variable `MQTT_SERVER` and `MQTT_PORT`

`docker run -e MQTT_SERVER=KAFKA_HOST -e MQTT_PORT=1883 -p 8080:8080 automatik/event-streams-sensors`

once this is done you can see the fully described service at:

http://localhost:8080/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it

This sample service utilizes MQTT features such as topic wildcard for subscribers to enable flexible
collection of data from sensors.

- water consumption information
- humidity information

Below is an example that runs through "happy path" to collect data from sensors taken from MQTT broker.


* Publish room 100 of building Main humidity data

- Topic `building/Main/100/humidity`
- Payload


````
{
  "ts" : 1608570416309,
  "val" : 48.0
}

````

* Publish room 100 of building Main water data

- Topic `building/Main/100/water`
- Payload


````
{
  "ts" : 1608570416309,
  "val" : 25.0
}

````

Repeat water consumption data more than 5 times to see the report being
calculated. At any point in time you can use service API to see current
state of the active instances.

---
**NOTE**

Why the format of the payload is as it is? The main reason is that it
follows [MQTT SmartHome](https://github.com/mqtt-smarthome/mqtt-smarthome)
 specification to provide some degree of standardisation.

---