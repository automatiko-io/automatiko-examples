package io.automatiko.examples.document.processor;

import java.io.File;
import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Message;

import io.automatiko.engine.api.io.InputConverter;
import io.automatiko.engine.services.utils.IoUtils;

@ApplicationScoped
public class ZipFileInputConverter implements InputConverter<ZipFile> {

    @Override
    public ZipFile convert(Object input) {
        Message msg = (Message) input;

        File file = msg.getBody(File.class);
        try {
            return new ZipFile(file.getName(), IoUtils.readBytes(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
