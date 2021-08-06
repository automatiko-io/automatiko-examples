package io.automatiko.examples.registration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class TestResults {

    private String receivedEvent;
    private ObjectNode receivedData;

    private CountDownLatch latch;

    public void appendResult(String type, ObjectNode data) {
        this.receivedEvent = type;
        this.receivedData = data;
        this.latch.countDown();
    }

    public void waitForEvent(int numberOfevents, long timeout) throws InterruptedException {
        this.latch = new CountDownLatch(numberOfevents);
        this.latch.await(timeout, TimeUnit.SECONDS);
    }

    public String getEventType() {
        return this.receivedEvent;
    }

    public ObjectNode getData() {
        return this.receivedData;
    }

    public void clear() {
        this.receivedData = null;
        this.receivedEvent = null;
        this.latch = null;
    }
}
