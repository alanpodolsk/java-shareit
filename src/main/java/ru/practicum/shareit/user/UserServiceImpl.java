package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDto getUser(Integer id) {
        User user = userRepository.getUser(id);
        if (user != null) {
            return UserMapper.toUserDto(user);
        } else {
            return null;
        }
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto != null) {
            userRepository.emailIsDuplicate(userDto.getEmail());
            return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto)));
        } else {
            throw new NoObjectException("Объект пользователя не может быть null");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteUser(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
        if (userDto != null) {
            User user = userRepository.getUser(id);
            if (user != null) {
                if (userDto.getName() != null) {
                    user.setName(userDto.getName());
                }
                if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
                    userRepository.emailIsDuplicate(userDto.getEmail());
                    user.setEmail(userDto.getEmail());
                }
                return UserMapper.toUserDto(userRepository.updateUser(user));
            } else {
                throw new NoObjectException("Пользователя с данным id не существует в программе");
            }
        } else {
            throw new NoObjectException("Объект пользователя не может быть null");
        }
    }

 /*   private void checkEmail(String email) {
        if (email == null) {
            throw new ValidationException("Email не может быть пустым");
       // } else if (userRepository.emailIsDuplicate(email)) {
        //    throw new ConflictException("Email должен быть уникальным");
        } else if (!email.contains("@")) {
            throw new ValidationException("Неверный формат Email");
        }
    } */
}
