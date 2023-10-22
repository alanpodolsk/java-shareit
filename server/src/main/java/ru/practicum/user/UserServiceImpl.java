package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NoObjectException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toUserDto(user.get());
        } else {
            throw new NoObjectException("Пользователь не найден в системе");
        }
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(UserMapper.toUserDto(user));
        }
        return userDtos;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer id) {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (userDto.getName() != null) {
                    user.setName(userDto.getName());
                }
                if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
                    user.setEmail(userDto.getEmail());
                }
                return UserMapper.toUserDto(userRepository.save(user));
            } else {
                throw new NoObjectException("Пользователя с данным id не существует в программе");
            }

    }


}

