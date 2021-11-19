package com.acme.notifications.model;

public class DataSet {

    private User user;

    private BackgroundCheckResult backgroundCheck;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BackgroundCheckResult getBackgroundCheck() {
        return backgroundCheck;
    }

    public void setBackgroundCheck(BackgroundCheckResult backgroundCheck) {
        this.backgroundCheck = backgroundCheck;
    }

    @Override
    public String toString() {
        return "DataSet [user=" + user + ", backgrounCheck=" + backgroundCheck + "]";
    }

}
