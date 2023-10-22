package ru.practicum.item.dto;

import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {


    public static OutputItemDto toOutputItemDto(Item item) {
        Integer requestId = null;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        UserDto owner = null;
        if (item.getOwner() != null) {
            owner = UserMapper.toUserDto(item.getOwner());
        }
        return new OutputItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                owner,
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
                null,
                null
        );
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
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

    public static List<OutputItemDto> toOutputItemDtos(List<Item> items) {
        List<OutputItemDto> outputItemDtos = new ArrayList<>();
        for (Item item : items) {
            outputItemDtos.add(ItemMapper.toOutputItemDto(item));
        }
        return outputItemDtos;
    }

}
