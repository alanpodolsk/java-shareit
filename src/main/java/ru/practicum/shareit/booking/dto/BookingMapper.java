package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
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
