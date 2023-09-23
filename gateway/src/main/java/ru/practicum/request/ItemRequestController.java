package ru.practicum.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {


    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@PathVariable Integer requestId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getRequest(requestId, userId);
    }


}
