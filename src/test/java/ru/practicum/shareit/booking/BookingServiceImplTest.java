package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    EasyRandom generator = new EasyRandom();
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    BookingService bookingService = new BookingServiceImpl(mockBookingRepository, mockItemRepository, mockUserRepository);

    @Test
    @DisplayName("Должен создать бронирование")
    void shouldCreateBooking() {
        //Arrange
        User user = generator.nextObject(User.class);
        Item item = generator.nextObject(Item.class);
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        item.setId(1);
        user.setId(1);
        UserDto userDto = UserMapper.toUserDto(user);
        OutputItemDto itemDto = ItemMapper.toOutputItemDto(item);
        booking.setItemId(1);
        booking.setStart(LocalDateTime.now().plusMinutes(5));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));
        //Act
        BookingDto savedBooking = bookingService.createBooking(booking, user.getId());
        //Assert
        Assertions.assertNotNull(savedBooking);
        Assertions.assertAll(
                () -> assertEquals(userDto, savedBooking.getBooker()),
                () -> assertEquals(itemDto, savedBooking.getItem()));
    }

    @Test
    @DisplayName("Ошибка создания бронирования - пустой объект бронирования")
    void shouldReturnNoObjectExceptionInCreateBookingIsNull() {
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.createBooking(null, 4)
        );
        Assertions.assertEquals("Не передан объект бронирования", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - пользователь не найден")
    void shouldReturnNoObjectExceptionInCreateBookingUserIsNull() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Пользователь не найден в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - собственная вещь")
    void shouldReturnNoObjectExceptionInCreateBookingOwnItem() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setOwner(user);
        item.setAvailable(true);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Это ваша собственная вещь", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - вещь недоступна")
    void shouldReturnNoObjectExceptionInCreateItemUnavailable() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setOwner(user);
        item.setAvailable(false);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        //Act
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Объект недоступен для бронирования", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - вещь не найдена")
    void shouldReturnValidationExceptionInCreateBookingItemIsNull() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        User user = generator.nextObject(User.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Данного объекта нет в БД", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - даты не заполнены")
    void shouldReturnValidationExceptionInCreateBookingDatesIsNull() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        booking.setStart(null);
        booking.setEnd(null);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setAvailable(true);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        //Act
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Даты бронирования должны быть заполнены", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - дата и время создания одинаковы")
    void shouldReturnValidationExceptionInCreateBookingZeroDuration() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(booking.getStart());
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setAvailable(true);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        //Act
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Бронирование должно длиться хотя бы 1 секунду", ex.getMessage());
    }

    @Test
    @DisplayName("Ошибка создания бронирования - дата создания позже окончания")
    void shouldReturnValidationExceptionInCreateBookingNegativeDuration() {
        //Arrange
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setAvailable(true);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        //Act
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.createBooking(booking, 4)
        );
        Assertions.assertEquals("Окончание бронирования должно быть позже старта бронирования", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть бронирование инициатору")
    void shouldReturnBookingForBooker() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        Booking booking = generator.nextObject(Booking.class);
        booking.setBooker(user);
        booking.setId(7);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        //Act
        Assertions.assertNotNull(bookingService.getBooking(7, 1));
    }

    @Test
    @DisplayName("Ошибка - бронирование не найдено в системе")
    void shouldReturnNoObjectExceptionGetBookingForBooker() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBooking(1, 1)
        );
        Assertions.assertEquals("Бронирование не найдено в системе", ex.getMessage());
    }


    @Test
    @DisplayName("Должен вернуть бронирование владельцу вещи")
    void shouldReturnBookingForOwner() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        booking.setItem(item);
        booking.setId(7);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        //Act
        Assertions.assertNotNull(bookingService.getBooking(7, 1));
    }

    @Test
    @DisplayName("Должен вернуть ошибку пользователю без прав просмотра")
    void shouldReturnNoObjectExceptionForInvalidUsed() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        Booking booking = generator.nextObject(Booking.class);
        booking.setBooker(user);
        booking.setId(7);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBooking(7, 4)
        );
        Assertions.assertEquals("Недостаточно прав на просмотр данного бронирования", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть ошибку пользователю - бронирование не найдено")
    void shouldReturnNoObjectExceptionForBookingIsNull() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        Booking booking = generator.nextObject(Booking.class);
        booking.setBooker(user);
        booking.setId(7);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBooking(7, 4)
        );
        Assertions.assertEquals("Бронирование не найдено в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Смена статуса - должен вернуть ошибку не найденного бронирования")
    void shouldNotFoundBookingInSetBookingStatus() {
        //Arrange
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.setBookingStatus(7, false, 1)
        );
        Assertions.assertEquals("Данное бронирование не найдено в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Смена статуса - должен вернуть ошибку некорректного статуса")
    void shouldReturnIncorrectStatusInSetBookingStatus() {
        //Arrange
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        item.setOwner(user);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.setBookingStatus(7, false, 1)
        );
        Assertions.assertEquals("Статус бронирования уже изменен", ex.getMessage());
    }

    @Test
    @DisplayName("Смена статуса - должен вернуть ошибку некорректного пользователя")
    void shouldReturnIncorrectChangerInSetBookingStatus() {
        //Arrange
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(4);
        item.setOwner(user);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.setBookingStatus(7, false, 1)
        );
        Assertions.assertEquals("Статус бронирования может изменять только собственник вещи", ex.getMessage());
    }

    @Test
    @DisplayName("Должен сменить статус на Подтверждено")
    void shouldSetBookingStatusApproved() {
        //Arrange
        User user = generator.nextObject(User.class);
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        user.setId(4);
        item.setOwner(user);
        booking.setId(3);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));
        //Act
        BookingDto patchedBooking = bookingService.setBookingStatus(3, true, 4);
        //Assert
        Assertions.assertNotNull(patchedBooking);
        assertEquals(BookingStatus.APPROVED, patchedBooking.getStatus());
    }

    @Test
    @DisplayName("Должен сменить статус на Отклонено")
    void shouldSetBookingStatusRejected() {
        //Arrange
        User user = generator.nextObject(User.class);
        Booking booking = generator.nextObject(Booking.class);
        Item item = generator.nextObject(Item.class);
        user.setId(4);
        item.setOwner(user);
        booking.setId(3);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(booking));
        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));
        //Act
        BookingDto patchedBooking = bookingService.setBookingStatus(3, false, 4);
        //Assert
        Assertions.assertNotNull(patchedBooking);
        assertEquals(BookingStatus.REJECTED, patchedBooking.getStatus());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - ALL")
    void shouldGetBookingsByUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerId(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "ALL", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - WAITING")
    void shouldGetBookingsByUserWaiting() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerIdAndStatus(Mockito.anyInt(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "WAITING", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - REJECTED")
    void shouldGetBookingsByUserRejected() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerIdAndStatus(Mockito.anyInt(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "REJECTED", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - PAST")
    void shouldGetBookingsByUserPast() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerIdAndEndIsBefore(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "PAST", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - FUTURE")
    void shouldGetBookingsByUserFuture() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerIdAndStartIsAfter(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "FUTURE", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - CURRENT")
    void shouldGetBookingsByUserCurrent() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.anyInt(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5, "CURRENT", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - INCORRECT_STATE")
    void shouldReturnIncorrectStateGetBookingsByUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingsByUser(1, "INCORRECT_STATE", 0, 50)
        );
        Assertions.assertEquals("Unknown state: INCORRECT_STATE", ex.getMessage());
    }


    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи")
    void shouldGetBookingsByItemsOwner() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdIn(Mockito.anyList(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "ALL", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - WAITING")
    void shouldGetBookingsByItemsOwnerWaiting() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdInAndStatus(Mockito.anyList(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "WAITING", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - REJECTED")
    void shouldGetBookingsByItemsOwnerRejected() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdInAndStatus(Mockito.anyList(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "REJECTED", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - PAST")
    void shouldGetBookingsByItemsOwnerPast() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdInAndEndIsBefore(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "PAST", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - FUTURE")
    void shouldGetBookingsByItemsOwnerFuture() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdInAndStartIsAfter(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "FUTURE", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - CURRENT")
    void shouldGetBookingsByItemsOwnerCurrent() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(Mockito.anyList(), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1, booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5, "CURRENT", 0, 50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(), List.of(booking1, booking2).size());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - INCORRECT_STATE")
    void shouldReturnIncorrectStateGetBookingsByItemsOwner() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingsByUsersItems(1, "INCORRECT_STATE", 0, 50)
        );
        Assertions.assertEquals("Unknown state: INCORRECT_STATE", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - Ошибка пагинации")
    void shouldReturnPaginationErrorsGetBookingsByItemsOwner() {
        //Arrange
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.getBookingsByUsersItems(1, "INCORRECT_STATE", -1, 50)
        );
        Assertions.assertEquals("Некорректные параметры пагинации", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя - Ошибка пагинации")
    void shouldReturnPaginationErrorsGetBookingsByUser() {
        //Arrange
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> bookingService.getBookingsByUser(1, "INCORRECT_STATE", -1, 50)
        );
        Assertions.assertEquals("Некорректные параметры пагинации", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - Ошибка пустого пользователя")
    void shouldReturnNoUserIdGetBookingsByItemsOwner() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBookingsByUsersItems(null, "ALL", 0, 50)
        );
        Assertions.assertEquals("Не передан ID пользователя", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - Ошибка пустого пользователя")
    void shouldReturnNoUserIdGetBookingsByUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBookingsByUser(null, "ALL", 0, 50)
        );
        Assertions.assertEquals("Не передан ID пользователя", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - Ошибка некорректного пользователя")
    void shouldReturnUserNotFoundGetBookingsByItemsOwner() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBookingsByUsersItems(2, "ALL", 0, 50)
        );
        Assertions.assertEquals("Указан некорректный пользователь", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований владельца вещи - Ошибка некорректного пользователя")
    void shouldReturnUserNotFoundGetBookingsByUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> bookingService.getBookingsByUser(2, "ALL", 0, 50)
        );
        Assertions.assertEquals("Указан некорректный пользователь", ex.getMessage());
    }
}