package io.automatiko.examples.sensors.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import io.automatiko.examples.sensors.Report;

@ApplicationScoped
public class ReportSendService {

    public void send(String building, List<Report> reports) {
        System.out.println("Sending reports for building '" + building + "' :: reports " + reports);
    }
}
