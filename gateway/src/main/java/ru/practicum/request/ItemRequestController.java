package ru.practicum.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {


    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Integer requestId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.getRequest(requestId, userId);
    }


}
