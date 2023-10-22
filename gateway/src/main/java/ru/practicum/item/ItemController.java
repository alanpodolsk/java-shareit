package ru.practicum.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.InputItemDto;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody InputItemDto inputItemDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.createItem(inputItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody CommentDto comment, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemClient.createComment(comment, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody InputItemDto inputItemDto, @RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return itemClient.updateItem(inputItemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Integer itemId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "50") Integer size) {
        return itemClient.getItemsByUser(userId, from, size);

    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "50") Integer size) {
        return itemClient.getItemsBySearch(text, from, size);
    }
}
