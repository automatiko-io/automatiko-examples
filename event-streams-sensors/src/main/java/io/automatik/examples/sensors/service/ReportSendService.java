package io.automatik.examples.sensors.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.automatik.examples.sensors.Report;

@ApplicationScoped
public class ReportSendService {

    public void send(String building, List<Report> reports) {
        System.out.println("Sending reports for building '" + building + "' :: reports " + reports);
    }
}
