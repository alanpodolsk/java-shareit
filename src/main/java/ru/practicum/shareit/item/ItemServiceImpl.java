package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.user.UserMemRepository;
import ru.practicum.shareit.item.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserMemRepository userMemRepository;
    private ItemRepository itemRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        if (itemDto == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userMemRepository.getUser(userId) == null) {
            throw new NoObjectException("Пользователь отсутствует");
        }
        Item item = ItemMapper.toItem(itemDto);
        checkItem(item);
        item.setOwner(userMemRepository.getUser(userId));
        itemRepository.createItem(item);
        return ItemMapper.toItemDto(itemRepository.getItem(item.getId()));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer userId, Integer itemId) {
        //проверка пользователя и добавляемого объекта
        if (itemDto == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        Item presentItem = itemRepository.getItem(itemId);
        if (presentItem == null) {
            throw new NoObjectException("Такого предмета нет в системе");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userMemRepository.getUser(userId) == null) {
            throw new NoObjectException("Пользователь отсутствует");
        } else if (!presentItem.getOwner().getId().equals(userId)) {
            throw new NoObjectException("Запрос на обновление может отправлять только пользователь");
        }
        //
        Item item = ItemMapper.toItem(itemDto);
        if (item.getName() != null) {
            presentItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            presentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            presentItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(presentItem));
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepository.getItem(itemId);
        if (item != null) {
            return ItemMapper.toItemDto(item);
        } else {
            return null;
        }
    }

    @Override
    public List<ItemDto> getItemsByUser(Integer userId) {
        User user = userMemRepository.getUser(userId);
        List<Item> items = itemRepository.getItemsByUser(user);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        List<Item> items = itemRepository.getItemsBySearch(text);
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            itemDtos.add(ItemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    private void checkItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Имя предмета не должно быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание предмета не должно быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность предмета должна быть указана");
        }
    }
}
