package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestsMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest){
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static List<ItemRequestDto> itemRequestDtoList(List<ItemRequest> itemRequests){
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest: itemRequests){
            itemRequestDtos.add(ItemRequestsMapper.toItemRequestDto(itemRequest));
        }
        return itemRequestDtos;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto){
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getCreated()
        );
    }
}
