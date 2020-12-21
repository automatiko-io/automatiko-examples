package io.automatik.examples.sensors.service;

import java.util.List;

import io.automatik.engine.api.Functions;
import io.automatik.examples.sensors.Report;

public class ReportFunctions implements Functions {

    public static boolean hasAnomalies(List<Report> reports) {
        return reports.stream().filter(r -> r.isLeakDetected()).count() > 0;
    }
}
