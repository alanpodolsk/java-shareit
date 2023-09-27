package ru.practicum.request;

import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ItemRequestDto;

public class ItemRequestValidations {
    public static void validateCreateRequest(ItemRequestDto itemRequestDto, Integer userId) {
        if (itemRequestDto == null || itemRequestDto.getDescription() == null || userId == null) {
            throw new ValidationException("Переданы не все обязательные параметры запроса");
        }
    }

    public static void validateGetRequest(Integer itemId, Integer userId) {
        if (itemId == null || userId == null) {
            throw new ValidationException("Переданы не все обязательные параметры запроса");
        }
    }

    public static void validateGetAllRequests(Integer from, Integer size, Integer userId) {
        if (userId == null) {
            throw new ValidationException("Переданы не все обязательные параметры запроса");
        }
        if (size < 1 || from < 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
    }

    public static void validateGetRequestsByUser(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Переданы не все обязательные параметры запроса");
        }
    }
}
