package ru.practicum.shareit.item.user;

import ru.practicum.shareit.item.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUser(Integer id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    void deleteUser(Integer id);

    UserDto updateUser(UserDto userDto, Integer id);
}
