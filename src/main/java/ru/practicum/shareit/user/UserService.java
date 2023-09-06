package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User getUser(Integer id);

    List<User> getAllUsers();

    User createUser(User user);

    void deleteUser(Integer id);

    User updateUser(UserDto user, Integer id);
}
