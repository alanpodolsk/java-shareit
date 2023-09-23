package ru.practicum.request;

import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Integer userId);

    List<ItemRequestDto> getRequestsByUser(Integer userId);

    List<ItemRequestDto> getAllRequests(Integer from, Integer size, Integer userId);

    ItemRequestDto getRequest(Integer itemId, Integer userId);
}
