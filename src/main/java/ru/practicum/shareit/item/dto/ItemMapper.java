package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static InputItemDto toItemDto(Item item) {
        return new InputItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null
        );
    }

    public static OutputItemDto toOutputItemDto(Item item) {
        Integer requestId = null;
        if (item.getRequest() != null){
            requestId = item.getRequest().getId();
        }
        return new OutputItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                requestId,
                null,
                null,
                new ArrayList<>()
        );
    }

    public static Item toItem(InputItemDto inputItemDto) {
        return new Item(
                inputItemDto.getId(),
                inputItemDto.getName(),
                inputItemDto.getDescription(),
                inputItemDto.getAvailable(),
                inputItemDto.getOwner(),
                null

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

    public static List<OutputItemDto> toOutputItemDtos(List<Item> items){
        List<OutputItemDto> outputItemDtos = new ArrayList<>();
        for (Item item: items){
            outputItemDtos.add(ItemMapper.toOutputItemDto(item));
        }
        return outputItemDtos;
    }

}
