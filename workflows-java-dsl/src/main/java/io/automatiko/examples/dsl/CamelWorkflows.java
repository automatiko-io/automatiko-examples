package io.automatiko.examples.dsl;

import io.automatiko.engine.api.Workflows;
import io.automatiko.engine.workflow.base.core.context.variable.Variable;
import io.automatiko.engine.workflow.builder.WorkflowBuilder;
import io.automatiko.engine.workflow.file.ByteArrayFile;

@Workflows
public class CamelWorkflows {

    public WorkflowBuilder fileWorkflow() {

        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("camel",
                "Sample workflow that uses Apache Camel for integration with other systems")
                .dataObject("file", ByteArrayFile.class, Variable.INTERNAL_TAG);

        builder.startOnMessage("file from folder").connector("camel")
                .endpointUri("file:target/documents/?include=.*\\\\.txt&noop=true")
                .toDataObject("file")
                .then().log("File processed", "Here is a file {}", "file")
                .then().end("Done");

        return builder;
    }
}
