package io.automatiko.examples.document.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.enterprise.context.ApplicationScoped;

import io.automatiko.engine.api.workflow.ServiceExecutionError;

@ApplicationScoped
public class ZipService {

    public List<TextFile> extract(ZipFile zip) {
        List<TextFile> documents = new ArrayList<TextFile>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zip.getContent())) {
            try (ZipInputStream zipIn = new ZipInputStream(bais)) {
                byte[] buffer = new byte[1024];

                ZipEntry entry = zipIn.getNextEntry();
                while (entry != null) {

                    if (entry.isDirectory() || !entry.getName().endsWith(".txt") || entry.getName().startsWith("__MACOSX")) {
                        entry = zipIn.getNextEntry();
                        continue;
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int len;
                    while ((len = zipIn.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    baos.close();

                    documents.add(new TextFile(entry.getName(), baos.toString(StandardCharsets.UTF_8)));

                    entry = zipIn.getNextEntry();
                }
            } catch (Exception e) {
                throw new ServiceExecutionError("unzipFailure", e);
            }
        } catch (IOException e1) {
            throw new ServiceExecutionError("unzipFailure", e1);
        }

        return documents;
    }
}
