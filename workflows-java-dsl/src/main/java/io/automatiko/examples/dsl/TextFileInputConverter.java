package io.automatiko.examples.dsl;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Message;

import io.automatiko.engine.api.io.InputConverter;
import io.automatiko.engine.workflow.file.ByteArrayFile;

@ApplicationScoped
public class TextFileInputConverter implements InputConverter<ByteArrayFile> {

    @Override
    public ByteArrayFile convert(Object input) {
        Message msg = (Message) input;

        byte[] content = msg.getBody(byte[].class);
        return new ByteArrayFile(msg.getHeader("CamelFileNameOnly", String.class), content);

    }

}
