package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserMemRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer currentId = 0;
    private final Map<Integer,String> uniqueEmails = new HashMap<>();

    private Integer getNewId() {
        currentId++;
        return currentId;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public User createUser(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        uniqueEmails.put(user.getId(),user.getEmail());
        return users.get(user.getId());
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        uniqueEmails.put(user.getId(),user.getEmail());
        return users.get(user.getId());
    }

    public void emailIsDuplicate(String email) {
        if (uniqueEmails.values().contains(email)) {
            throw new ConflictException("Email должен быть уникальным");
        }
    }

    public void deleteUser(Integer id) {
        users.remove(id);
        uniqueEmails.remove(id);
    }
}
