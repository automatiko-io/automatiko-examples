package io.automatiko.engine.app.rest.model;

import java.util.List;

public class BackgroundCheckResult {

    private String status;

    private String username;

    private List<String> comments;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "BackgroundCheckResult [status=" + status + ", username=" + username + ", comments=" + comments + "]";
    }

}
