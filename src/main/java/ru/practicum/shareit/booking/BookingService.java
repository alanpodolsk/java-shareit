package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoForCreate bookingDto, Integer userId);
    BookingDto getBooking(Integer bookingId, Integer userId);
    BookingDto setBookingStatus(Integer id, boolean approved, Integer userId);
    List<BookingDto> getBookingsByUser(Integer userId, String state);
    List<BookingDto> getBookingsByUsersItems(Integer userId, String state);
}
