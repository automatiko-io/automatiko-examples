package io.automatiko.examples.support.incidents;

public class Incident {

    private String title;

    private String description;

    private String severity;

    private Reporter reporter;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public String toString() {
        return "Incident [title=" + title + ", description=" + description + ", severity=" + severity + ", reporter=" + reporter
                + "]";
    }

}
