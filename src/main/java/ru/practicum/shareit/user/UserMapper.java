package ru.practicum.shareit.user;


import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail());
    }

    public static User toUser(UserDto user) {
        return new User(
                null,
                user.getName(),
                user.getEmail());
    }
}