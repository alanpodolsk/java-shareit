package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemRepository {
    Map<Integer, Item> items = new HashMap<>();
}
