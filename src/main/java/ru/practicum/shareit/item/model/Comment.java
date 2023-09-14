package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.user.model.User;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Comment {
    Integer id;
    String text;
    User author;
    Item item;
    Instant created;
}
