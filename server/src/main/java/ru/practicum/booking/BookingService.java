package ru.practicum.booking;

import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoForCreate;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDtoForCreate bookingDto, Integer userId);

    BookingDto getBooking(Integer bookingId, Integer userId);

    BookingDto setBookingStatus(Integer id, boolean approved, Integer userId);

    List<BookingDto> getBookingsByUser(Integer userId, String state, Integer start, Integer size);

    List<BookingDto> getBookingsByUsersItems(Integer userId, String state, Integer start, Integer size);
}
