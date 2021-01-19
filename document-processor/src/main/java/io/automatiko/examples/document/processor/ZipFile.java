package io.automatiko.examples.document.processor;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ZipFile {

    private String name;

    @JsonIgnore
    private byte[] content;

    public ZipFile() {
    }

    public ZipFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ZipFile [name=" + name + ", content=" + content.length + "]";
    }

}
