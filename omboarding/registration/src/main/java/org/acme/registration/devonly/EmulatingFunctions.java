//package org.acme.registration.devonly;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import jakarta.inject.Inject;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.automatiko.engine.api.event.EventSource;
//import io.automatiko.engine.app.rest.model.BackgroundCheckResult;
//import io.automatiko.engine.app.rest.model.User;
//import io.quarkus.funqy.Funq;
//import io.quarkus.funqy.knative.events.CloudEvent;
//import io.quarkus.funqy.knative.events.CloudEventMapping;
//
///**
// * A helpful emulating functions of the notification service so it can be used locally
// * activated only when running in dev mode
// */
//public class EmulatingFunctions {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(EmulatingFunctions.class);
//
//    @Inject
//    EventSource eventSource;
//
//    @Funq
//    @CloudEventMapping(trigger = "org.acme.background.checks")
//    public void backgroundCheck(CloudEvent<User> event) {
//        LOGGER.info("Running background check on user {}", event.data());
//
//        BackgroundCheckResult result = new BackgroundCheckResult();
//
//        if (!event.data().getUsername().contains("x")) {
//            // use correlation based on event data
//            result.setUsername(event.data().getUsername());
//            result.setStatus("OK");
//            result.setComments(Collections.emptyList());
//
//            eventSource.produce("org.acme.background.checks.ok", "org.acme.background.checks/12345", result);
//        } else {
//            // use direct correlation by workflow instance id
//            String replyId = "";
//            if (event.source().contains("/")) {
//                replyId = event.source().split("/")[1];
//            }
//            result.setUsername(event.data().getUsername());
//            result.setStatus("FAILED");
//            result.setComments(Arrays.asList("Incorrect username"));
//            eventSource.produce("org.acme.background.checks.failed", "org.acme.background.checks/12345", result,
//                    replyId);
//        }
//    }
//
//    @Funq
//    @CloudEventMapping(trigger = "com.acme.notifications.email")
//    public void emailNotification(CloudEvent<DataSet> event) {
//        LOGGER.info("Sending email notification for user {}", event.data());
//    }
//
//    @Funq
//    @CloudEventMapping(trigger = "com.acme.notifications.sms")
//    public void smsNotification(CloudEvent<DataSet> event) {
//        LOGGER.info("Sending sms notification for user {}", event.data());
//    }
//
//    @Funq
//    @CloudEventMapping(trigger = "com.acme.notifications.slack")
//    public void slackNotification(CloudEvent<DataSet> event) {
//        LOGGER.info("Sending slack notification for user {}", event.data());
//    }
//
//    static class DataSet {
//
//        private User user;
//
//        private BackgroundCheckResult backgroundCheck;
//
//        public User getUser() {
//            return user;
//        }
//
//        public void setUser(User user) {
//            this.user = user;
//        }
//
//        public BackgroundCheckResult getBackgroundCheck() {
//            return backgroundCheck;
//        }
//
//        public void setBackgroundCheck(BackgroundCheckResult backgroundCheck) {
//            this.backgroundCheck = backgroundCheck;
//        }
//
//        @Override
//        public String toString() {
//            return "DataSet [user=" + user + ", backgrounCheck=" + backgroundCheck + "]";
//        }
//
//    }
//
//}
