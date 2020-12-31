# Weather conditions example

## Overview 

this is an example showing service orchestration use case. It implements common scenario for workflows -
that multiple services needs to be invoked and depending on the results another set of services might be called again.

See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/weather.html)

## Run it

The only thing that is needed is Jaeger (for observability/opentracing) running that will be accessible. One can be 
started with

````
docker run -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 jaegertracing/all-in-one:latest
````

NOTE: To run this service API keys for [IPSTACK](https://ipstack.com/) and [Open Weather API](https://openweathermap.org/) is required. These can be freely obtained from the websites.

`docker run -e QUARKUS_JAEGER_ENDPOINT=http://HOST:14268/api/traces -e IPSTACK_API_KEY=YOUR_IPSTACK_KEY -e OPEN_WEATHER_API_KEY=YOUR_OPEN_WEATHER_KEY -p 8080:8080 automatiko/weather-conditions`

once this is done you can see the fully described service at:

http://localhost:8080/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it


### Start new instance for an IP

- Http Method: `POST`
- Endpoint: `http://localhost:8080/weatherConditions?businessKey=testing`
- Payload

````
{
  "ip": "134.201.250.155"
}
````

Above example will load data for Los Angeles and should return similar response

````
{
  "id": "testing",
  "location": {
    "ip": "134.201.250.155",
    "type": "ipv4",
    "continent_code": "NA",
    "continent_name": "North America",
    "country_code": "US",
    "country_name": "United States",
    "region_code": "CA",
    "region_name": "California",
    "city": "Los Angeles",
    "zip": "90012",
    "latitude": 34.06555,
    "longitude": -118.24054,
    "location": {
      "geoname_id": 5368361,
      "capital": "Washington D.C.",
      "languages": [
        {
          "code": "en",
          "name": "English",
          "native": "English"
        }
      ]
    },
    "time_zone": null,
    "currency": null,
    "connection": null
  },
  "forecast": {
    "coord": {
      "lon": -118.24,
      "lat": 34.05
    },
    "weather": [
      {
        "id": 800,
        "main": "Clear",
        "description": "clear sky",
        "icon": "01n"
      }
    ],
    "base": "stations",
    "main": {
      "temp": 279.6,
      "pressure": 1016,
      "humidity": 70,
      "temp_min": 279.15,
      "temp_max": 280.15,
      "sea_level": null,
      "grnd_level": null
    },
    "visibility": 10000,
    "wind": {
      "speed": 2.57,
      "deg": 22
    },
    "clouds": {
      "all": 1
    },
    "rain": null,
    "snow": null,
    "dt": 1609423602,
    "sys": {
      "type": 1,
      "id": 3694,
      "message": null,
      "country": "US",
      "sunrise": 1609426703,
      "sunset": 1609462430
    },
    "id": 5368361,
    "name": "Los Angeles",
    "cod": 200
  }
}
````