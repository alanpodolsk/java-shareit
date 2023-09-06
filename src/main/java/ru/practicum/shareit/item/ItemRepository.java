package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemRepository {
    static Map<Integer, Item> items = new HashMap<>();
    private static Integer currentId = 0;

    public static Integer getNewId() {
        currentId++;
        return currentId;
    }
}
