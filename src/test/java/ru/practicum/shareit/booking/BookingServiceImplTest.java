package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                ()-> assertEquals(user,savedBooking.getBooker()),
                ()-> assertEquals(item,savedBooking.getItem()));
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
        Assertions.assertNotNull(bookingService.getBooking(7,1));
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
        Assertions.assertNotNull(bookingService.getBooking(7,1));
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
                ()-> bookingService.getBooking(7,4)
        );
        Assertions.assertEquals("Недостаточно прав на просмотр данного бронирования", ex.getMessage());
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
        BookingDto patchedBooking = bookingService.setBookingStatus(3,true,4);
        //Assert
        Assertions.assertNotNull(patchedBooking);
        assertEquals(BookingStatus.APPROVED,patchedBooking.getStatus());
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
        BookingDto patchedBooking = bookingService.setBookingStatus(3,false,4);
        //Assert
        Assertions.assertNotNull(patchedBooking);
        assertEquals(BookingStatus.REJECTED,patchedBooking.getStatus());
    }

    @Test
    @DisplayName("Должен вернуть перечень бронирований пользователя")
    void shouldGetBookingsByUser() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(5);
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockBookingRepository.findByBookerId(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking1,booking2)));
        //Act
        List<BookingDto> savedBookingDtos = bookingService.getBookingsByUser(5,"ALL",0,50);
        //Assert
        Assertions.assertEquals(savedBookingDtos.size(),List.of(booking1,booking2).size());
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
                    .thenReturn(new PageImpl<>(List.of(booking1,booking2)));
            //Act
            List<BookingDto> savedBookingDtos = bookingService.getBookingsByUsersItems(5,"ALL",0,50);
            //Assert
            Assertions.assertEquals(savedBookingDtos.size(),List.of(booking1,booking2).size());
        }
}