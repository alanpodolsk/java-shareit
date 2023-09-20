package ru.practicum.shareit.user;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    EasyRandom generator = new EasyRandom();
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    UserService userService = new UserServiceImpl(mockUserRepository);

    @Test
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
    void shouldGetAllUsers() {
        //Arrange
        User user1 = generator.nextObject(User.class);
        User user2 = generator.nextObject(User.class);
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(List.of(user1,user2));
        //Act
        List<UserDto> users = userService.getAllUsers();
        //Assert
        Assertions.assertEquals(users.size(),2);
    }

    @Test
    void shouldCreateUser() {
        //Arrange
        UserDto userDto = generator.nextObject(UserDto.class);
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        //Assert
        Assertions.assertNotNull(userService.createUser(userDto));
    }

    @Test
    void shouldDeleteUser() {
        //Act
        userService.deleteUser(1);
        //Assert
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(Mockito.anyInt());
    }

    @Test
    void updateUser() {
        UserDto userDto = generator.nextObject(UserDto.class);
        userDto.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(UserMapper.toUser(userDto)));
        Mockito.when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        //Assert
        Assertions.assertNotNull(userService.updateUser(userDto,1));
    }
}