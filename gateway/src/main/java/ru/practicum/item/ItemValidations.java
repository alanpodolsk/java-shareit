package ru.practicum.item;

import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.InputItemDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;

public class ItemValidations {
    public static void validateCreateItem(InputItemDto inputItemDto, Integer userId) {
        if (inputItemDto == null || userId == null) {
            throw new NoObjectException("Некорректное заполнение параметров запроса");
        }
        checkItem(inputItemDto);
    }

    public static void validateUpdateItem(InputItemDto inputItemDto, Integer userId, Integer itemId) {
        if (inputItemDto == null || userId == null || itemId == null) {
            throw new NoObjectException("Некорректное заполнение параметров запроса");
        }
    }

    public static void validateGetItem(Integer userId, Integer itemId) {
        if (userId == null || itemId == null) {
            throw new NoObjectException("Некорректное заполнение параметров запроса");
        }
    }

    public static void validateGetItemsByUser(Integer userId, Integer start, Integer size) {
        if (size < 1 || start < 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
        if (userId == null) {
            throw new NoObjectException("Некорректное заполнение параметров запроса");
        }
    }

    public static void validateGetItemsBySearch(String text, Integer start, Integer size) {
        if (size < 1 || start < 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
    }

    public static void validateCreateComment(Comment comment, Integer userId, Integer itemId){
        if(comment == null || comment.getText().isBlank() || userId == null || itemId == null){
            throw new ValidationException("Некорректное заполнение параметров запроса");
        }
    }


    private static void checkItem(InputItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Имя предмета не должно быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание предмета не должно быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность предмета должна быть указана");
        }
    }


}
