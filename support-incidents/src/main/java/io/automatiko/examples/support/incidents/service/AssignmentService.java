package io.automatiko.examples.support.incidents.service;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.examples.support.incidents.Incident;

@ApplicationScoped
public class AssignmentService {

    public String assignIncident(Incident incident) {

        return "first-line";
    }
}
