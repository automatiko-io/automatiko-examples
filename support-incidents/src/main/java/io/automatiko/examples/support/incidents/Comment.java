package io.automatiko.examples.support.incidents;

public class Comment {

    private String text;

    private String user;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment [text=" + text + ", user=" + user + "]";
    }
}
