package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.item.user.model.User;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    Integer id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
    List<Comment> comments;
}
