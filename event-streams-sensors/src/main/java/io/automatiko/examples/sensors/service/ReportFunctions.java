package io.automatiko.examples.sensors.service;

import java.util.List;

import io.automatiko.engine.api.Functions;
import io.automatiko.examples.sensors.Report;

public class ReportFunctions implements Functions {

    public static boolean hasAnomalies(List<Report> reports) {
        return reports.stream().filter(r -> r.isLeakDetected()).count() > 0;
    }
}
