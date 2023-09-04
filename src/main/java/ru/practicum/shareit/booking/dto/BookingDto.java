package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class BookingDto {
        Integer id;
        LocalDateTime start;
        LocalDateTime end;
        Item item;
        User booker;
        BookingStatus status;
    }

