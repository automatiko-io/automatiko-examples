package com.acme.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.notifications.model.DataSet;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventMapping;

public class NotificationFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationFunctions.class);

    @Funq
    @CloudEventMapping(trigger = "com.acme.notifications.email")
    public void emailNotification(CloudEvent<DataSet> event) {
        LOGGER.info("Sending email notification for user {}", event.data());
    }

    @Funq
    @CloudEventMapping(trigger = "com.acme.notifications.sms")
    public void smsNotification(CloudEvent<DataSet> event) {
        LOGGER.info("Sending sms notification for user {}", event.data());
    }

    @Funq
    @CloudEventMapping(trigger = "com.acme.notifications.slack")
    public void slackNotification(CloudEvent<DataSet> event) {
        LOGGER.info("Sending slack notification for user {}", event.data());
    }
}
