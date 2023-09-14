package ru.practicum.shareit.item.user;


import ru.practicum.shareit.item.user.dto.UserDto;
import ru.practicum.shareit.item.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static User toUser(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail());
    }
}