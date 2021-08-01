package io.automatiko.examples.support.incidents;

public class Reporter {

    private String name;

    private String email;

    private String company;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Reporter [name=" + name + ", email=" + email + ", company=" + company + "]";
    }

}
