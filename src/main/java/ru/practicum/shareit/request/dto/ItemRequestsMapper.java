package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemRequestsMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        UserDto requester = null;
        if (itemRequest.getRequester() != null) {
            requester = UserMapper.toUserDto(itemRequest.getRequester());
        }
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requester,
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        User requester = null;
        if (itemRequestDto.getRequester() != null) {
            requester = UserMapper.toUser(itemRequestDto.getRequester());
        }
        return new ItemRequest(

                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requester,
                itemRequestDto.getCreated()
        );
    }
}
