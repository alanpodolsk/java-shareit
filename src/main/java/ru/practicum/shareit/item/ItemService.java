package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Integer userId);

    Item updateItem(Item item, Integer userId, Integer itemId);

    Item getItem(Integer itemId);

    List<Item> getItemsByUser(Integer userId);

    List<Item> getItemsBySearch(String text);
}
