package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Integer userId);

    ItemDto updateItem(ItemDto item, Integer userId, Integer itemId);

    ItemDtoWithBooking getItem(Integer itemId, Integer userId);

    List<ItemDtoWithBooking> getItemsByUser(Integer userId);

    List<ItemDto> getItemsBySearch(String text);
}
