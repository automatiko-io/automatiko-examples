package io.automatiko.examples.vacation.requests;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Employee extends PanacheEntity {

    @NotNull
    public String firstName;

    @NotNull
    public String lastName;

    @NotNull
    @Email
    public String email;

    @NotNull
    public Date startedAt;

    @NotNull
    public String department;

    public String manager;

    @Override
    public String toString() {
        return "Employee [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", startedAt=" + startedAt
                + ", department=" + department + ", manager=" + manager + "]";
    }

}
