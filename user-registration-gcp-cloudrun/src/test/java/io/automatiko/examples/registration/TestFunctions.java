package io.automatiko.examples.registration;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestFunctions {

    @Inject
    TestResults results;

    @io.quarkus.funqy.knative.events.CloudEventMapping(trigger = "google.cloud.pubsub.topic.v1.messagePublished", attributes = @io.quarkus.funqy.knative.events.EventAttribute(name = "source", value = "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration.userregistered"))
    @io.quarkus.funqy.Funq()
    public void userRegistred(ObjectNode resource) {
        System.out.println("User registered " + resource);
        results.appendResult("userregistered", resource);
    }

    @io.quarkus.funqy.knative.events.CloudEventMapping(trigger = "google.cloud.pubsub.topic.v1.messagePublished", attributes = @io.quarkus.funqy.knative.events.EventAttribute(name = "source", value = "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration.alreadyregistered"))
    @io.quarkus.funqy.Funq()
    public void userAlreadyRegistred(ObjectNode resource) {
        System.out.println("User was already registered " + resource);
        results.appendResult("already-registered", resource);
    }

    @io.quarkus.funqy.knative.events.CloudEventMapping(trigger = "google.cloud.pubsub.topic.v1.messagePublished", attributes = @io.quarkus.funqy.knative.events.EventAttribute(name = "source", value = "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration.invaliddata"))
    @io.quarkus.funqy.Funq()
    public void invalidData(ObjectNode resource) {
        System.out.println("Invalid data " + resource);
        results.appendResult("invalid", resource);
    }

    @io.quarkus.funqy.knative.events.CloudEventMapping(trigger = "google.cloud.pubsub.topic.v1.messagePublished", attributes = @io.quarkus.funqy.knative.events.EventAttribute(name = "source", value = "//pubsub.googleapis.com/projects/CHANGE_ME/topics/io.automatiko.examples.userRegistration.registrationfailed"))
    @io.quarkus.funqy.Funq()
    public void serverError(ObjectNode resource) {
        System.out.println("Registration failed " + resource);
        results.appendResult("failed", resource);
    }
}
