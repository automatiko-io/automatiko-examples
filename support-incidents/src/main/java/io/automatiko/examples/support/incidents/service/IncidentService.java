package io.automatiko.examples.support.incidents.service;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.examples.support.incidents.Comment;
import io.automatiko.examples.support.incidents.Incident;

@ApplicationScoped
public class IncidentService {

    private List<String> priorityCompanies = Arrays.asList("apache");

    public Incident classify(Incident incident) {

        if (incident.getReporter().getCompany() == null) {
            incident.setSeverity("low");
        } else if (priorityCompanies.contains(incident.getReporter().getCompany().toLowerCase())) {
            incident.setSeverity("urgent");
        } else {
            incident.setSeverity("normal");
        }

        return incident;
    }

    public String extractStatus(String currentStatus, List<Comment> comments) {

        if (!comments.isEmpty()) {
            Comment lastComment = comments.get(comments.size() - 1);

            if (lastComment.getText().toLowerCase().contains("resolve")) {
                return SupportCaseFunctions.RESOLVED_STATUS;
            }

            if (lastComment.getText().toLowerCase().contains("close")) {
                return SupportCaseFunctions.CLOSED_STATUS;
            }
        }

        return currentStatus;
    }
}
