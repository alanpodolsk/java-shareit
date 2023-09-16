package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest()
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(ItemMapper.toCommentDto(comment));
        }
        return commentDtos;
    }

}
