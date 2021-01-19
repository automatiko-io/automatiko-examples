package io.automatiko.examples.document.processor;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.engine.api.workflow.ServiceExecutionError;

@ApplicationScoped
public class TextService {

    public String clasify(TextFile file) {

        if (file.getName().startsWith("password")) {
            return "confidential";
        }

        if (file.getContent().length() > 100) {
            return "report";
        }

        if (file.getContent().contains("hello")) {
            return "greeting";
        }

        throw new ServiceExecutionError("unclassified");
    }
}
