package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    ItemService itemService;
    @PostMapping
    public ItemDto createItem(Item item){
        return itemService.createItem(item);
    }

    @PatchMapping
    public ItemDto updateItem(Item item){
        return itemService.updateItem(item);
    }
}
