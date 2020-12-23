package io.automatiko.examples.orders.services;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.automatiko.examples.orders.ChangeSet;
import io.automatiko.examples.orders.Item;

@ApplicationScoped
public class ItemService {

    public Item process(ChangeSet change, List<Item> items) {

        Item item = change.getItem();

        if (item.getQuantity() == 0) {
            // remove item since quantity is 0

            items.remove(item);

            return null;
        } else if (item.getQuantity() > 0) {
            // add or merge

            Optional<Item> found = items.stream().filter(i -> i.getArticleId().equals(item.getArticleId())).findFirst();

            if (found.isEmpty()) {
                items.add(item);

                return item;
            } else {

                Item existing = found.get();

                existing.setQuantity(existing.getQuantity() + item.getQuantity());

                return null;
            }
        } else if (item.getQuantity() < 0) {
            // reduce quantity or remove completely

            Optional<Item> found = items.stream().filter(i -> i.getArticleId().equals(item.getArticleId())).findFirst();

            if (found.isPresent()) {
                Item existing = found.get();
                existing.setQuantity(existing.getQuantity() + item.getQuantity());// item's quantity is negative

                if (existing.getQuantity() <= 0) {
                    items.remove(existing);
                    return null;
                }

                return null;
            }

        }
        return null;
    }
}
