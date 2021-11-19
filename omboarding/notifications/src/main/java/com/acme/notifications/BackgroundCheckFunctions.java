package com.acme.notifications;

import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acme.notifications.model.BackgroundCheckResult;
import com.acme.notifications.model.User;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventBuilder;
import io.quarkus.funqy.knative.events.CloudEventMapping;

public class BackgroundCheckFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundCheckFunctions.class);

    @Funq
    @CloudEventMapping(trigger = "org.acme.background.checks")
    public CloudEvent<BackgroundCheckResult> backgroundCheck(CloudEvent<User> event) {
        LOGGER.info("Running background check on user {}", event.data());

        BackgroundCheckResult result = new BackgroundCheckResult();

        if (!event.data().getUsername().contains("x")) {
            // use correlation based on event data
            result.setUsername(event.data().getUsername());
            result.setStatus("OK");
            result.setComments(Collections.emptyList());
            return CloudEventBuilder.create().type("org.acme.background.checks.ok").build(result);
        } else {
            String replyId = "";
            if (event.source().contains("/")) {
                replyId = event.source().split("/")[1];
            }
            result.setUsername(event.data().getUsername());
            result.setStatus("FAILED");
            result.setComments(Arrays.asList("Incorrect username"));
            return CloudEventBuilder.create().subject(replyId).type("org.acme.background.checks.failed").build(result);
        }
    }

}
