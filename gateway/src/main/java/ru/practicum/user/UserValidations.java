package ru.practicum.user;

import ru.practicum.exception.NoObjectException;
import ru.practicum.user.dto.UserDto;

public class UserValidations {
    public static void validateCreateUser(UserDto userDto){
        if (userDto == null){
            throw new NoObjectException("Объект пользователя не может быть null");
        }
    }

    public static void validateGetUser(Integer id){
        if (id == null){
            throw new NoObjectException("ID пользователя не может быть null");
        }
    }

    public static void validateUpdateUser(UserDto userDto, Integer id){
        if (id == null || userDto == null){
            throw new NoObjectException("Некорректно заполнены параметры запроса");
        }
    }
}
