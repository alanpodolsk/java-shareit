package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        if (itemDto == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userRepository.findById(userId).isEmpty()) {
            throw new NoObjectException("Пользователь отсутствует");
        }
        Item item = ItemMapper.toItem(itemDto);
        checkItem(item);
        item.setOwner(userRepository.findById(userId).get());
        itemRepository.save(item);
        return ItemMapper.toItemDto(itemRepository.findById(item.getId()).get());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer userId, Integer itemId) {
        //проверка пользователя и добавляемого объекта
        Item presentItem;
        if (itemDto == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        Optional<Item> presentItemOpt = itemRepository.findById(itemId);
        if (presentItemOpt.isEmpty()) {
            throw new NoObjectException("Такого предмета нет в системе");
        } else
            presentItem = presentItemOpt.get();
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userRepository.findById(userId).isEmpty()) {
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
        return ItemMapper.toItemDto(itemRepository.save(presentItem));
    }

    @Override
    public ItemDtoWithBooking getItem(Integer itemId, Integer userId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(itemOpt.get());
            if (userId == itemDtoWithBooking.getOwner().getId()){
                Optional<Booking> lastBooking = bookingRepository.findLastBooking(itemId);
                Optional<Booking> nextBooking = bookingRepository.findNextBooking(itemId);
                if (lastBooking.isPresent()){
                    itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDtoForItemList(lastBooking.get()));
                }
                if (nextBooking.isPresent()){
                    itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDtoForItemList(nextBooking.get()));
                }
            }
            return itemDtoWithBooking;
        } else {
            throw new NoObjectException("Объект не найден в системе");
        }
    }

    @Override
    public List<ItemDtoWithBooking> getItemsByUser(Integer userId) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<ItemDtoWithBooking> itemDtos = new ArrayList<>();
        for (Item item : items) {
            Optional<Booking> lastBooking = bookingRepository.findLastBooking(item.getId());
            Optional<Booking> nextBooking = bookingRepository.findNextBooking(item.getId());
            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item);
            if (lastBooking.isPresent()){
                itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDtoForItemList(lastBooking.get()));
            }
            if (nextBooking.isPresent()){
                itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDtoForItemList(nextBooking.get()));
            }
            itemDtos.add(itemDtoWithBooking);
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (text.isBlank()){
            return new ArrayList<ItemDto>();
        }
        List<Item> items = itemRepository.search(text);
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
