package io.automatiko.examples.vacation.requests.service;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.examples.vacation.requests.Employee;
import io.automatiko.examples.vacation.requests.Request;

@ApplicationScoped
public class NotificationService {

    public void approvedNotification(Employee employee, Request request) {
        System.out.println("Vacation request " + request + " has been approved, email sent to " + employee.email);
    }

    public void rejectedNotification(Employee employee, Request request) {
        System.out.println("Vacation request " + request + " has been rejected (" + request.comment + "), email sent to "
                + employee.email);
    }

    public void notEnoughDaysNotification(Employee employee, Request request) {
        System.out.println("Vacation request " + request + " has been exceeded eligible days, email sent to "
                + employee.email);
    }
}
