package io.automatiko.examples.support.incidents.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.automatiko.examples.support.incidents.Comment;
import io.automatiko.examples.support.incidents.Incident;

@ApplicationScoped
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    public void notifyReporter(Incident incident, String incidentKey) {
        LOGGER.info("Incident {} has been registered with key {}", incident, incidentKey);
    }

    public void notifySupportTeam(Incident incident, String incidentKey, String supportteam) {
        LOGGER.info("Incident {} has been assigned to your support team with key {}", incident, incidentKey);
    }

    public void notifyMentioned(Incident incident, String incidentKey, String supportteam, List<Comment> comments) {

        Comment lastComment = comments.get(comments.size() - 1);

        if (lastComment.getText().contains("@reporter")) {
            LOGGER.info("Hello {}, there is a new comment for your support case {}", incident.getReporter().getName(),
                    incidentKey);
        }

        if (lastComment.getText().contains("@support")) {
            LOGGER.info("Hello {}, there is a new comment for support case {}", supportteam, incidentKey);
        }
    }
}
