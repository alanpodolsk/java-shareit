package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public OutputItemDto createItem(InputItemDto inputItemDto, Integer userId) {
        if (inputItemDto == null) {
            throw new NoObjectException("Объект item не должен быть пустым");
        }
        if (userId == null) {
            throw new NoObjectException("ID пользователя не должен быть пустым");
        } else if (userRepository.findById(userId).isEmpty()) {
            throw new NoObjectException("Пользователь отсутствует");
        }
        Item item = ItemMapper.toItem(inputItemDto);
        checkItem(item);
        item.setOwner(userRepository.findById(userId).get());
        if (inputItemDto.getRequestId() != null){
            Optional<ItemRequest> itemRequestOptional = itemRequestRepository.findById(inputItemDto.getRequestId());
            itemRequestOptional.ifPresent(item::setRequest);
        }
        itemRepository.save(item);
        return ItemMapper.toOutputItemDto(itemRepository.findById(item.getId()).get());
    }

    @Override
    public OutputItemDto updateItem(InputItemDto inputItemDto, Integer userId, Integer itemId) {
        //проверка пользователя и добавляемого объекта
        Item presentItem;
        if (inputItemDto == null) {
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
        Item item = ItemMapper.toItem(inputItemDto);
        if (item.getName() != null) {
            presentItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            presentItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            presentItem.setAvailable(item.getAvailable());
        }
        return ItemMapper.toOutputItemDto(itemRepository.save(presentItem));
    }

    @Override
    public OutputItemDto getItem(Integer itemId, Integer userId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            OutputItemDto outputItemDto = ItemMapper.toOutputItemDto(itemOpt.get());
            if (userId.equals(outputItemDto.getOwner().getId())) {
                Optional<Booking> lastBooking = bookingRepository.findLastBooking(itemId);
                Optional<Booking> nextBooking = bookingRepository.findNextBooking(itemId);
                lastBooking.ifPresent(booking -> outputItemDto.setLastBooking(BookingMapper.toBookingDtoForItemList(booking)));
                nextBooking.ifPresent(booking -> outputItemDto.setNextBooking(BookingMapper.toBookingDtoForItemList(booking)));

            }
            outputItemDto.setComments(ItemMapper.toCommentDtoList(commentRepository.findByItemId(itemId)));
            return outputItemDto;
        } else {
            throw new NoObjectException("Объект не найден в системе");
        }
    }

    @Override
    public List<OutputItemDto> getItemsByUser(Integer userId, Integer start, Integer size) {
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId, PageRequest.of(start, size)).getContent();
        List<OutputItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            OutputItemDto outputItemDto = ItemMapper.toOutputItemDto(item);
            itemDtos.add(itemEnrichment(outputItemDto,true));
        }
        return itemDtos;
    }

    @Override
    public List<OutputItemDto> getItemsBySearch(String text, Integer start, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.search(text, PageRequest.of(start, size)).getContent();
        List<OutputItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            OutputItemDto outputItemDto = ItemMapper.toOutputItemDto(item);
            itemDtos.add(itemEnrichment(outputItemDto, false));
        }
        return itemDtos;
    }

    @Override
    public CommentDto createComment(Comment comment, Integer userId, Integer itemId) {
        if (comment == null || comment.getText().isBlank()) {
            throw new ValidationException("Комментарий не должен быть пустым");
        }
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (itemOpt.isEmpty() || userOpt.isEmpty()) {
            throw new NoObjectException("Проверьте корректность ID пользователя или предмета");
        }
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndStartIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.size() == 0) {
            throw new ValidationException("Не найдено подходящее бронирование");
        }
        comment.setItem(itemOpt.get());
        comment.setAuthor(userOpt.get());
        comment.setCreated(LocalDateTime.now());
        return ItemMapper.toCommentDto(commentRepository.save(comment));
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

    private OutputItemDto itemEnrichment(OutputItemDto outputItemDto, boolean isOwner){
        if(isOwner){
            Optional<Booking> lastBooking = bookingRepository.findLastBooking(outputItemDto.getId());
            Optional<Booking> nextBooking = bookingRepository.findNextBooking(outputItemDto.getId());
            lastBooking.ifPresent(booking -> outputItemDto.setLastBooking(BookingMapper.toBookingDtoForItemList(booking)));
            nextBooking.ifPresent(booking -> outputItemDto.setNextBooking(BookingMapper.toBookingDtoForItemList(booking)));
        }
        outputItemDto.setComments(ItemMapper.toCommentDtoList(commentRepository.findByItemId(outputItemDto.getId())));
        return outputItemDto;
    }

}
