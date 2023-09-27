package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Integer id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<OutputItemDto> items;
}
