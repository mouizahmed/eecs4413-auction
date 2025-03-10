package com.a2.bidding;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class ItemService {
    private final List<Item> items = new ArrayList<>();
    private final AtomicLong itemIdCounter = new AtomicLong(1);

    public List<Item> searchItems(String keyword) {
        return items.stream()
                .filter(item -> item.getName().contains(keyword))
                .toList();
    }

    public Item addItem(Item item) {
        item.setId(itemIdCounter.getAndIncrement());
        items.add(item);
        return item;
    }

    public List<Item> getAllItems() {
        return items;
    }


}
