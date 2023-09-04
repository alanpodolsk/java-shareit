package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
@Component
public class UserRepository {
    Map<Integer, User> users = new HashMap<>();
}
