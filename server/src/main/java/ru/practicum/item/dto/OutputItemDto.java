package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.booking.dto.BookingDtoForItemList;
import ru.practicum.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputItemDto {
    Integer id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    Boolean available;
    UserDto owner;
    Integer requestId;
    BookingDtoForItemList lastBooking;
    BookingDtoForItemList nextBooking;
    List<CommentDto> comments;
}
