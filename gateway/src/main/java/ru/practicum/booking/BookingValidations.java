package ru.practicum.booking;

import ru.practicum.booking.dto.BookingDtoForCreate;
import ru.practicum.booking.model.BookingStateStatus;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidations {

    public static void validateCreateBooking(BookingDtoForCreate bookingDto, Integer userId) {
        if (bookingDto == null || userId == null) {
            throw new NoObjectException("Некорректно заполнены входные параметры");
        }
        checkBookingDates(LocalDateTime.now(), bookingDto);
    }

    public static void validateSetBookingStatus(Integer id, boolean approved, Integer userId) {
        if (id == null | userId == null) {
            throw new NoObjectException("Некорректно заполнены входные параметры");
        }
    }

    public static void validateGetBookings(Integer userId, String state, Integer start, Integer size) {
        if (size < 1 || start < 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
        if (userId == null) {
            throw new NoObjectException("Не передан ID пользователя");
        }
        try {
            BookingStateStatus enumState;
            enumState = Enum.valueOf(BookingStateStatus.class, state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }

    }

    private static void checkBookingDates(LocalDateTime checkTime, BookingDtoForCreate booking) {
        if (booking.getEnd() == null || booking.getStart() == null) {
            throw new ValidationException("Даты бронирования должны быть заполнены");
        }
        if (booking.getStart().isBefore(checkTime) || booking.getEnd().isBefore(checkTime)) {
            throw new ValidationException("Даты бронирования должны быть позже, чем время проверки");
        } else if (booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException(("Бронирование должно длиться хотя бы 1 секунду"));
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Окончание бронирования должно быть позже старта бронирования");
        }
    }
}
