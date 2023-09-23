package ru.practicum.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.exception.NoObjectException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;


class UserServiceImplTest {
    EasyRandom generator = new EasyRandom();
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    UserService userService = new UserServiceImpl(mockUserRepository);

    @Test
    @DisplayName("Должен вернуть пользователя")
    void shouldGetUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        //Assert
        Assertions.assertNotNull(userService.getUser(5));
    }

    @Test
    @DisplayName("Пользователь не найден")
    void shouldReturnNoObjectExceptionGetUserNotFound() {
        //Arrange
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> userService.getUser(1)
        );
        Assertions.assertEquals("Пользователь не найден в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть всех пользователей системы")
    void shouldGetAllUsers() {
        //Arrange
        User user1 = generator.nextObject(User.class);
        User user2 = generator.nextObject(User.class);
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(List.of(user1, user2));
        //Act
        List<UserDto> users = userService.getAllUsers();
        //Assert
        Assertions.assertEquals(users.size(), 2);
    }

    @Test
    @DisplayName("Должен создать пользователя")
    void shouldCreateUser() {
        //Arrange
        UserDto userDto = generator.nextObject(UserDto.class);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        //Assert
        Assertions.assertNotNull(userService.createUser(userDto));
    }

    @Test
    @DisplayName("Ошибка создания пользователя - пустой параметр")
    void shouldReturnNoObjectExceptionCreateUserEmptyInput() {
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> userService.createUser(null)
        );
        Assertions.assertEquals("Объект пользователя не может быть null", ex.getMessage());
    }

    @Test
    @DisplayName("Должен удалить пользователя")
    void shouldDeleteUser() {
        //Act
        userService.deleteUser(1);
        //Assert
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(Mockito.anyInt());
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void updateUser() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(UserMapper.toUser(userDto)));
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        //Assert
        Assertions.assertNotNull(userService.updateUser(userDto, 1));
    }

    @Test
    @DisplayName("Ошибка обновления пользователя - пустой параметр")
    void shouldReturnNoObjectExceptionUpdateUserEmptyInput() {
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> userService.updateUser(null,1)
        );
        Assertions.assertEquals("Объект пользователя не может быть null", ex.getMessage());
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void shouldReturnNoObjectExceptionUpdateUserNotFound() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> userService.updateUser(userDto,1)
        );
        Assertions.assertEquals("Пользователя с данным id не существует в программе", ex.getMessage());
    }
}