package ru.practicum.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Create;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.InputItemDto;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.item.model.Comment;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public OutputItemDto createItem(@RequestBody @Validated(Create.class) InputItemDto inputItemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createItem(inputItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody Comment comment, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.createComment(comment, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public OutputItemDto updateItem(@RequestBody InputItemDto inputItemDto, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemService.updateItem(inputItemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public OutputItemDto getItem(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public List<OutputItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "50") Integer size) {
        return itemService.getItemsByUser(userId, from, size);

    }

    @GetMapping("/search")
    public List<OutputItemDto> getItemsBySearch(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "50") Integer size) {
        return itemService.getItemsBySearch(text, from, size);
    }
}
