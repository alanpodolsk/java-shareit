package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody  @Validated(Create.class) ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsByUser(userId);

    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam(defaultValue = "") String text) {
        return itemService.getItemsBySearch(text);
    }
}
