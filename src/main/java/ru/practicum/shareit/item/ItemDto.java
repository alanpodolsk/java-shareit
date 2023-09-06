package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    String name;
    String description;
    Boolean available;
    Integer requestId;

    public static class ItemMapper {
        public static ItemDto toItemDto(Item item) {
            return new ItemDto(
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest() != null ? item.getRequest().getId() : null
            );
        }

    }
}
