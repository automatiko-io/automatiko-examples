package io.automatiko.examples.sensors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Humidity {

    @JsonProperty("val")
    private double value;

    @JsonProperty("ts")
    private long timestamp;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Humidity [value=" + value + ", timestamp=" + timestamp + "]";
    }

}
