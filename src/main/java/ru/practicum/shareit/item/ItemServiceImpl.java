package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private ItemRepository itemRepository;

    @Override
    public Item createItem(Item item, Integer userId) {
        if (item == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userService.getUser(userId) == null) {
            throw new NoObjectException("Пользователь отсутствует");
        }
        checkItem(item);
        itemRepository.currentId++;
        item.setId(itemRepository.currentId);
        item.setOwner(userService.getUser(userId));
        itemRepository.items.put(item.getId(), item);
        return itemRepository.items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item, Integer userId, Integer itemId) {
        //проверка пользователя и добавляемого объекта
        if (item == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        Item presentItem = getItem(itemId);
        if (presentItem == null) {
            throw new NoObjectException("Такого предмета нет в системе");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userService.getUser(userId) == null) {
            throw new NoObjectException("Пользователь отсутствует");
        } else if (presentItem.getOwner().getId() != userId) {
            throw new NoObjectException("Запрос на обновление может отправлять только пользователь");
        }
        //
        if (item.getName() != null) {
            presentItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            presentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            presentItem.setAvailable(item.getAvailable());
        }
        itemRepository.items.put(presentItem.getId(), presentItem);
        return itemRepository.items.get(itemId);
    }

    @Override
    public Item getItem(Integer itemId) {
        return itemRepository.items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUser(Integer userId) {
        List<Item> items = new ArrayList<>();
        User user = userService.getUser(userId);
        if (user != null) {
            for (Item item : itemRepository.items.values()) {
                if (item.getOwner().equals(user)) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        List<Item> items = new ArrayList<>();
        if (text.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.items.values()) {
            if (item.getAvailable() && (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                items.add(item);
            }
        }
        return items;
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
