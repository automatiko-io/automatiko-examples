package io.automatiko.examples.dsl;

import java.util.concurrent.TimeUnit;

import io.automatiko.engine.api.Workflows;
import io.automatiko.engine.workflow.builder.EventSplitNodeBuilder;
import io.automatiko.engine.workflow.builder.WorkflowBuilder;

@Workflows
public class MessageWorkflows {

    public WorkflowBuilder splitOnEvents() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("splitOnEvents",
                "Sample workflow with exclusive split on events")
                .dataObject("customer", Customer.class);

        EventSplitNodeBuilder split = builder.start("start here").then()
                .log("log", "About to wait for events")
                .thenSplitOnEvents("wait for events");

        split.onMessage("events").toDataObject("customer").then().log("after message", "Message arrived").then()
                .end("done on message");
        split.onTimer("timeout").after(30, TimeUnit.SECONDS).then().log("after timeout", "Timer fired").then()
                .end("done on timeout");

        return builder;
    }

    public WorkflowBuilder receiveAndSendMessages() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("messages",
                "Sample workflow with with Apache Kafka messages (consume and produce)");
        builder.dataObject("customer", Customer.class)
                .startOnMessage("customers").toDataObject("customer").topic("customers")
                .then()
                .sendMessage("new message").fromDataObject("customer").topic("published")
                .then()
                .log("log message", "Logged customer with id {}", "customer")
                .then()
                .waitOnMessage("updates").toDataObject("customer")
                .then()
                .endWithMessage("done").fromDataObject("customer");

        return builder;
    }

    public WorkflowBuilder sendMultipleMessages() {
        // just create any instance so it can be referenced from workflow definition
        Customer item = new Customer();

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("multipleMessages",
                "Sample workflow with that sends multiple Apache Kafka messages");
        builder.listDataObject("customers", Customer.class)
                .start("customers")
                .then()
                .sendMessage("new message").topic("published").repeat("customers", "item")
                .expressionAsInput(String.class, () -> item.getCustomerId())
                .then()
                .log("log message", "Logged customer")
                .then()
                .endWithMessage("lastUpdate").topic("published").expressionAsInput(String.class, () -> "completed");

        return builder;
    }
}
