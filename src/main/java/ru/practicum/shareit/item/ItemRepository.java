package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer currentId = 0;

    public Integer getNewId() {
        currentId++;
        return currentId;
    }

    public Item createItem(Item item) {
        item.setId(getNewId());
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    public void deleteItem(Integer id) {
        items.remove(id);
    }

    public Item getItem(Integer id) {
        return items.get(id);
    }

    public List<Item> getItemsByUser(User user) {
        List userItems = new ArrayList<>();
        if (user != null) {
            for (Item item : items.values()) {
                if (item.getOwner().equals(user)) {
                    userItems.add(item);
                }
            }
        }
        return userItems;
    }

    public List<Item> getItemsBySearch(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items.values()) {
                if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                    searchItems.add(item);
                }
            }
        }
        return searchItems;
    }
}