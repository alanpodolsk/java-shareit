package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Integer userId);

    ItemDto updateItem(ItemDto item, Integer userId, Integer itemId);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getItemsByUser(Integer userId);

    List<ItemDto> getItemsBySearch(String text);
}
