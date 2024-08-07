package io.automatiko.examples.dsl;

import static io.automatiko.engine.workflow.base.core.context.variable.Variable.INPUT_TAG;

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

        split.onMessage("events").connector("kafka").toDataObject("customer").then().log("after message", "Message arrived")
                .then()
                .end("done on message");
        split.onTimer("timeout").after(30, TimeUnit.SECONDS).then().log("after timeout", "Timer fired").then()
                .end("done on timeout");

        return builder;
    }

    public WorkflowBuilder receiveAndSendMessages() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("messages",
                "Sample workflow with with Apache Kafka messages (consume and produce)");
        builder.dataObject("customer", Customer.class)
                .startOnMessage("customers").connector("kafka").toDataObject("customer").topic("customers")
                .then()
                .sendMessage("new message").connector("kafka").fromDataObject("customer").topic("published")
                .then()
                .log("log message", "Logged customer with id {}", "customer")
                .then()
                .waitOnMessage("updates").connector("kafka").toDataObject("customer")
                .then()
                .endWithMessage("done").connector("kafka").fromDataObject("customer");

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
                .sendMessage("new message").connector("kafka").topic("published").repeat("customers", "item")
                .expressionAsInput(String.class, () -> item.getCustomerId())
                .then()
                .log("log message", "Logged customer")
                .then()
                .endWithMessage("lastUpdate").connector("kafka").topic("published")
                .expressionAsInput(String.class, () -> "completed");

        return builder;
    }

    public WorkflowBuilder receiveMessagesDirect() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("receivedirectmessages",
                "Sample workflow with direct messages (consume and produce)");
        builder.dataObject("customer", Customer.class)
                .startOnMessage("Receive data from channel").connector(WorkflowBuilder.Connectors.DIRECT).channel("mydata")
                .toDataObject("customer")
                .filter((eventData, message) -> ((Customer) eventData).getCustomerId().equals("123"))
                .then()
                .log("log message", "DIRECT:: Logged customer with id {}", "customer")
                .then()
                .end("done");

        return builder;
    }

    public WorkflowBuilder sendMessagesDirect() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("senddirectmessages",
                "Sample workflow with direct messages (consume and produce)");
        builder.dataObject("customer", Customer.class, INPUT_TAG)
                .start("start")
                .then()
                .sendMessage("send message to channel").connector(WorkflowBuilder.Connectors.DIRECT).channel("mydata")
                .fromDataObject("customer")
                .then()
                .log("log message", "DIRECT:: Logged customer with id {}", "customer")
                .then()
                .end("done");

        return builder;
    }
}
