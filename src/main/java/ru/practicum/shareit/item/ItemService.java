package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Item item);
    ItemDto updateItem(Item item);
    ItemDto getItem(Integer itemId);
    List<ItemDto> getItemsByUser(Integer userId);
    List<ItemDto> getItemsBySearch(String text);
}
