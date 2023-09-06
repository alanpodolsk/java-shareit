package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Item createItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.updateItem(item, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable Integer itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<Item> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemsByUser(userId);

    }

    @GetMapping("/search")
    public List<Item> getItemsBySearch(@RequestParam(defaultValue = "") String text) {
        return itemService.getItemsBySearch(text);
    }
}
