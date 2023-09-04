package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    UserDto getUser(Integer Id);

    List<UserDto> getAllUsers();

    UserDto createUser(User user);

    void deleteUser(Integer id);

    UserDto updateUser(User user);
}
