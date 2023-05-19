package io.automatiko.examples.weather.conditions.service;

import jakarta.enterprise.context.ApplicationScoped;

import io.automatiko.engine.api.workflow.workitem.WorkItemExecutionError;

@ApplicationScoped
public class NotificationService {

    private static int counter = 0;

    public void forwardForecast(Object forecast, Object location) {
        counter++;

        if (counter % 3 == 0) {
            System.out.println("Forwarding weather forecast random failure...");
            throw new WorkItemExecutionError("ForwardFail");
        }
        System.out.println("Forwarding weather forecast");
    }

    public void notify(Object forecast, Object location) {
        System.out.println("Nofity about weather forecast");
    }
}
