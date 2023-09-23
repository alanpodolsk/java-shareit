package ru.practicum.booking.dto;



import ru.practicum.booking.model.Booking;
import ru.practicum.item.dto.ItemMapper;
import ru.practicum.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toOutputItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static BookingDtoForItemList toBookingDtoForItemList(Booking booking) {
        return new BookingDtoForItemList(
                booking.getId(),
                booking.getBooker().getId()
        );
    }


    public static Booking toBooking(BookingDtoForCreate bookingDtoForCreate) {
        return new Booking(
                null,
                bookingDtoForCreate.getStart(),
                bookingDtoForCreate.getEnd(),
                null,
                null,
                null);
    }

    public static List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(BookingMapper.toBookingDto(booking));
        }
        return bookingDtos;
    }


}
