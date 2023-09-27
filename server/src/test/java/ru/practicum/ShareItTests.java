package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoForCreate;
import ru.practicum.item.ItemService;
import ru.practicum.item.dto.InputItemDto;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.request.ItemRequestService;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ShareItTests {
    EasyRandom generator = new EasyRandom();
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;

    @Test
    @DisplayName("Проверка вывода пользователей")
    void shouldGetAllUsers() {
        //Arrange
        User user1 = new User(null, "Вася", "vasya@mail.ru");
        User user2 = new User(null, "Василий", "vasya1@mail.ru");
        UserDto savedUser1 = userService.createUser(UserMapper.toUserDto(user1));
        UserDto savedUser2 = userService.createUser(UserMapper.toUserDto(user2));
        //Act
        List<UserDto> savedUsers = userService.getAllUsers();
        //Assert
        Assertions.assertArrayEquals(new UserDto[]{savedUser1, savedUser2}, savedUsers.toArray());
        //Act
        userService.deleteUser(savedUser1.getId());
        savedUsers = userService.getAllUsers();
        Assertions.assertArrayEquals(new UserDto[]{savedUser2}, savedUsers.toArray());
    }

    @Test
    @DisplayName("Проверка вывода предметов пользователей")
    void shouldGetAllItemsByUser() {
        //Arrange
        UserDto user1 = new UserDto(null, "Вася", "vasya@mail.ru");
        UserDto user2 = new UserDto(null, "Василий", "vasya1@mail.ru");
        InputItemDto item1 = generator.nextObject(InputItemDto.class);
        InputItemDto item2 = generator.nextObject(InputItemDto.class);
        InputItemDto item3 = generator.nextObject(InputItemDto.class);

        userService.createUser(user1);
        userService.createUser(user2);

        OutputItemDto savedItem1 = itemService.createItem(item1, 1);
        OutputItemDto savedItem2 = itemService.createItem(item2, 2);
        OutputItemDto savedItem3 = itemService.createItem(item3, 1);

        //Act
        List<OutputItemDto> firstUserItems = itemService.getItemsByUser(1, 0, 50);
        //Assert
        Assertions.assertArrayEquals(new OutputItemDto[]{savedItem1, savedItem3}, firstUserItems.toArray());
    }

    @Test
    @DisplayName("Проверка вывода предметов по поиску")
    void shouldGetAllItemsBySearch() {
        //Arrange
        UserDto user1 = new UserDto(null, "Вася", "vasya@mail.ru");
        UserDto user2 = new UserDto(null, "Василий", "vasya1@mail.ru");
        InputItemDto item1 = generator.nextObject(InputItemDto.class);
        InputItemDto item2 = generator.nextObject(InputItemDto.class);
        InputItemDto item3 = generator.nextObject(InputItemDto.class);
        item1.setName("Шуруповерт");
        item3.setDescription("Биты для шуруповерта");

        userService.createUser(user1);
        userService.createUser(user2);

        OutputItemDto savedItem1 = itemService.createItem(item1, 1);
        OutputItemDto savedItem2 = itemService.createItem(item2, 2);
        OutputItemDto savedItem3 = itemService.createItem(item3, 1);

        //Act
        List<OutputItemDto> firstUserItems = itemService.getItemsBySearch("шуруповерт", 0, 50);
        //Assert
        Assertions.assertArrayEquals(new OutputItemDto[]{savedItem1, savedItem3}, firstUserItems.toArray());
    }

    @Test
    @DisplayName("Должен вывести все запросы")
    void shouldGetRequests() {
        //Arrange
        UserDto user1 = new UserDto(null, "Вася", "vasya@mail.ru");
        UserDto user2 = new UserDto(null, "Василий", "vasya1@mail.ru");
        userService.createUser(user1);
        userService.createUser(user2);

        ItemRequestDto requestDto1 = generator.nextObject(ItemRequestDto.class);
        ItemRequestDto requestDto2 = generator.nextObject(ItemRequestDto.class);
        ItemRequestDto requestDto3 = generator.nextObject(ItemRequestDto.class);

        ItemRequestDto savedRequestDto1 = itemRequestService.createRequest(requestDto1, 1);
        ItemRequestDto savedRequestDto2 = itemRequestService.createRequest(requestDto2, 2);
        ItemRequestDto savedRequestDto3 = itemRequestService.createRequest(requestDto3, 1);

        //Act
        List<ItemRequestDto> otherSavedRequests = itemRequestService.getAllRequests(0, 50, 1);
        List<ItemRequestDto> ownSavedRequests = itemRequestService.getRequestsByUser(1);

        //Assert
        Assertions.assertAll(
                () -> assertEquals(savedRequestDto2.getId(), otherSavedRequests.get(0).getId()),
                () -> assertEquals(savedRequestDto3.getId(), ownSavedRequests.get(1).getId()),
                () -> assertEquals(savedRequestDto1.getId(), ownSavedRequests.get(0).getId()));
    }

    @Test
    @DisplayName("Должен вывести все бронирования пользователя")
    void shouldGetAllBookingsByUser() {
        //Arrange
        UserDto user1 = new UserDto(null, "Вася", "vasya@mail.ru");
        UserDto user2 = new UserDto(null, "Василий", "vasya1@mail.ru");
        UserDto user3 = new UserDto(null, "Василий1", "vasya11@mail.ru");

        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);

        InputItemDto item1 = generator.nextObject(InputItemDto.class);
        InputItemDto item2 = generator.nextObject(InputItemDto.class);
        InputItemDto item3 = generator.nextObject(InputItemDto.class);
        item1.setAvailable(true);
        item2.setAvailable(true);
        item3.setAvailable(true);

        OutputItemDto savedItem1 = itemService.createItem(item1, 3);
        OutputItemDto savedItem2 = itemService.createItem(item2, 3);
        OutputItemDto savedItem3 = itemService.createItem(item3, 3);

        BookingDtoForCreate booking1 = generator.nextObject(BookingDtoForCreate.class);
        BookingDtoForCreate booking2 = generator.nextObject(BookingDtoForCreate.class);
        BookingDtoForCreate booking3 = generator.nextObject(BookingDtoForCreate.class);

        booking1.setStart(LocalDateTime.now().plusMinutes(5));
        booking1.setEnd(LocalDateTime.now().plusMinutes(15));
        booking2.setStart(LocalDateTime.now().plusMinutes(6));
        booking2.setEnd(LocalDateTime.now().plusMinutes(16));
        booking3.setStart(LocalDateTime.now().plusMinutes(7));
        booking3.setEnd(LocalDateTime.now().plusMinutes(17));

        booking1.setItemId(savedItem1.getId());
        booking2.setItemId(savedItem2.getId());
        booking3.setItemId(savedItem3.getId());

        BookingDto savedBooking1 = bookingService.createBooking(booking1, 1);
        BookingDto savedBooking2 = bookingService.createBooking(booking2, 2);
        BookingDto savedBooking3 = bookingService.createBooking(booking3, 1);

        //Act
        List<BookingDto> ownBookings = bookingService.getBookingsByUser(1, "ALL", 0, 50);

        //Assert
        Assertions.assertAll(
                () -> assertEquals(savedBooking3.getId(), ownBookings.get(0).getId()),
                () -> assertEquals(savedBooking1.getId(), ownBookings.get(1).getId())
        );
    }

    @Test
    @DisplayName("Должен вывести все ожидающие бронирования вещей пользователя")
    void shouldGetWaitingBookingsByUserItems() {
        //Arrange
        UserDto user1 = new UserDto(null, "Вася", "vasya@mail.ru");
        UserDto user2 = new UserDto(null, "Василий", "vasya1@mail.ru");
        UserDto user3 = new UserDto(null, "Василий1", "vasya11@mail.ru");

        userService.createUser(user1);
        userService.createUser(user2);
        userService.createUser(user3);

        InputItemDto item1 = generator.nextObject(InputItemDto.class);
        InputItemDto item2 = generator.nextObject(InputItemDto.class);
        InputItemDto item3 = generator.nextObject(InputItemDto.class);
        item1.setAvailable(true);
        item2.setAvailable(true);
        item3.setAvailable(true);

        OutputItemDto savedItem1 = itemService.createItem(item1, 3);
        OutputItemDto savedItem2 = itemService.createItem(item2, 3);
        OutputItemDto savedItem3 = itemService.createItem(item3, 3);

        BookingDtoForCreate booking1 = generator.nextObject(BookingDtoForCreate.class);
        BookingDtoForCreate booking2 = generator.nextObject(BookingDtoForCreate.class);
        BookingDtoForCreate booking3 = generator.nextObject(BookingDtoForCreate.class);

        booking1.setStart(LocalDateTime.now().plusMinutes(5));
        booking1.setEnd(LocalDateTime.now().plusMinutes(15));
        booking2.setStart(LocalDateTime.now().plusMinutes(6));
        booking2.setEnd(LocalDateTime.now().plusMinutes(16));
        booking3.setStart(LocalDateTime.now().plusMinutes(7));
        booking3.setEnd(LocalDateTime.now().plusMinutes(17));

        booking1.setItemId(savedItem1.getId());
        booking2.setItemId(savedItem2.getId());
        booking3.setItemId(savedItem3.getId());

        BookingDto savedBooking1 = bookingService.createBooking(booking1, 1);
        BookingDto savedBooking2 = bookingService.createBooking(booking2, 2);
        BookingDto savedBooking3 = bookingService.createBooking(booking3, 1);
        savedBooking2 = bookingService.setBookingStatus(savedBooking2.getId(), true, 3);

        //Act
        List<BookingDto> ownItemsBookings = bookingService.getBookingsByUsersItems(3, "WAITING", 0, 50);

        //Assert
        Assertions.assertAll(
                () -> assertEquals(savedBooking3.getId(), ownItemsBookings.get(1).getId()),
                () -> assertEquals(savedBooking1.getId(), ownItemsBookings.get(0).getId())
        );
    }


}
