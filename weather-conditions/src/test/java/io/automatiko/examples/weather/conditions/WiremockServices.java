package io.automatiko.examples.weather.conditions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class WiremockServices implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        stubFor(get(urlEqualTo("/1.1.1.1?access_key=abc&language=en"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"ip\":\"1.1.1.1\",\"type\":\"ipv4\",\"continent_code\":\"NA\",\"continent_name\":\"North America\",\"country_code\":\"US\",\"country_name\":\"United States\",\"region_code\":\"CA\",\"region_name\":\"California\",\"city\":\"Los Angeles\",\"zip\":\"90012\",\"latitude\":34.0655517578125,\"longitude\":-118.24053955078125,\"location\":{\"geoname_id\":5368361,\"capital\":\"Washington D.C.\",\"languages\":[{\"code\":\"en\",\"name\":\"English\",\"native\":\"English\"}],\"country_flag\":\"http:\\/\\/assets.ipstack.com\\/flags\\/us.svg\",\"country_flag_emoji\":\"\\ud83c\\uddfa\\ud83c\\uddf8\",\"country_flag_emoji_unicode\":\"U+1F1FA U+1F1F8\",\"calling_code\":\"1\",\"is_eu\":false}}")));

        stubFor(get(urlEqualTo("/2.2.2.2?access_key=abc&language=en"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"ip\":\"192.168.100.28\",\"type\":null,\"continent_code\":null,\"continent_name\":null,\"country_code\":null,\"country_name\":null,\"region_code\":null,\"region_name\":null,\"city\":null,\"zip\":null,\"latitude\":null,\"longitude\":null,\"location\":{\"geoname_id\":null,\"capital\":null,\"languages\":null,\"country_flag\":null,\"country_flag_emoji\":null,\"country_flag_emoji_unicode\":null,\"calling_code\":null,\"is_eu\":null}}")));

        stubFor(get(urlEqualTo("/3.3.3.3?access_key=abc&language=en"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(503)));

        stubFor(get(urlEqualTo("/data/2.5/weather?appid=xyz&q=Los+Angeles"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"coord\":{\"lon\":19.47,\"lat\":50.16},\"weather\":[{\"id\":741,\"main\":\"Fog\",\"description\":\"fog\",\"icon\":\"50d\"}],\"base\":\"stations\",\"main\":{\"temp\":274.42,\"feels_like\":271.22,\"temp_min\":273.71,\"temp_max\":275.37,\"pressure\":1009,\"humidity\":84},\"visibility\":400,\"wind\":{\"speed\":1.5,\"deg\":300},\"clouds\":{\"all\":90},\"dt\":1609405213,\"sys\":{\"type\":1,\"id\":1701,\"country\":\"PL\",\"sunrise\":1609396882,\"sunset\":1609426129},\"timezone\":3600,\"id\":3083111,\"name\":\"Trzebinia\",\"cod\":200}")));

        stubFor(get(urlMatching(".*")).atPriority(10).willReturn(aResponse().proxiedFrom("http://api.ipstack.com")));

        Map<String, String> map = new HashMap<>();
        map.put("ipstackapi/mp-rest/url", wireMockServer.baseUrl());
        map.put("openweathermapapi/mp-rest/url", wireMockServer.baseUrl() + "/data/2.5");

        return map;
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}
