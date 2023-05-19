package io.automatiko.examples.orders.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import jakarta.enterprise.context.ApplicationScoped;

import io.automatiko.examples.orders.Order;
import io.automatiko.examples.orders.Shippment;

@ApplicationScoped
public class ShippmentService {

    public Shippment requestShippment(Order order) {
        System.out.println("Shippment requested for order " + order.getOrderNumber());

        return new Shippment("123456", Date.from(LocalDate.now().plusDays(5).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()));
    }

    public void cancelShippment(Shippment shippment) {
        System.out.println("Shippment canceled " + shippment.getShippmentId());
    }
}
