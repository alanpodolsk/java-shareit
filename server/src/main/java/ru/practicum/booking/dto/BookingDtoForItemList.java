package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItemList {
    @NotNull
    Integer id;
    Integer bookerId;
}
