package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    OutputItemDto createItem(InputItemDto item, Integer userId);

    OutputItemDto updateItem(InputItemDto item, Integer userId, Integer itemId);

    OutputItemDto getItem(Integer itemId, Integer userId);

    List<OutputItemDto> getItemsByUser(Integer userId, Integer start, Integer size);

    List<OutputItemDto> getItemsBySearch(String text, Integer start, Integer size);

    CommentDto createComment(Comment comment, Integer userId, Integer itemId);
}
