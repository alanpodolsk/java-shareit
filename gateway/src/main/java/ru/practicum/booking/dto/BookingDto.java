package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class BookingDto {
        private Integer id;
        private LocalDateTime start;
        private LocalDateTime end;
        private OutputItemDto item;
        private UserDto booker;
        private BookingStatus status;
    }

