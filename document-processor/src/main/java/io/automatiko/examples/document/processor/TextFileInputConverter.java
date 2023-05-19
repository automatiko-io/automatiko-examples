package io.automatiko.examples.document.processor;

import java.io.File;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Message;

import io.automatiko.engine.api.io.InputConverter;
import io.automatiko.engine.services.utils.IoUtils;

@ApplicationScoped
public class TextFileInputConverter implements InputConverter<TextFile> {

    @Override
    public TextFile convert(Object input) {
        Message msg = (Message) input;

        File file = msg.getBody(File.class);
        return new TextFile(file.getName(), IoUtils.readFileAsString(file));
    }

}
