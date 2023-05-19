package io.automatiko.examples.document.processor;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService {

    public void sendGreetings(TextFile file) {

        System.out.println("Sending greetings::");
        System.out.println("***********************************");
        System.out.println(file.getContent());
        System.out.println("***********************************");
    }
}
