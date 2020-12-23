package io.automatiko.examples.orders.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.examples.orders.Item;
import io.automatiko.examples.orders.Order;
import io.automatiko.examples.orders.Shippment;

@ApplicationScoped
public class NotificationService {

    public void sendConfirmationEmail(Order order, List<Item> items) {
        System.out.println("Order " + order.getOrderNumber() + " has been successfully placed, email sent to "
                + order.getCustomer().getEmail());
    }

    public void sendCancelationEmail(Order order, List<Item> items) {
        System.out.println(
                "Order " + order.getOrderNumber() + " has been canceled, email sent to " + order.getCustomer().getEmail());
    }

    public void sendShippmentEmail(Order order, List<Item> items, Shippment shippment) {
        System.out.println(
                "Order " + order.getOrderNumber() + " has been shipped, email sent to " + order.getCustomer().getEmail());
    }
}
