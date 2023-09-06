package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepository {
    static Map<Integer, User> users = new HashMap<>();
    static Integer currentId = 0;
    static List<String> uniqueEmails = new ArrayList<>();
}
