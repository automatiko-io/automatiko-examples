package io.automatiko.examples.orders;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(classNames = { "java.util.Date", "io.automatiko.examples.orders.ChangeSet" })
public class ChangeSet {

    private Order order;

    private Item item;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ChangeSet [order=" + order + ", item=" + item + "]";
    }

}
