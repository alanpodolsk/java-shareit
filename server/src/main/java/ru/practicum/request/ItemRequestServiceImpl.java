package ru.practicum.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.dto.ItemMapper;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.item.model.Item;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestsMapper;
import ru.practicum.request.model.ItemRequest;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Integer userId) {
        if (itemRequestDto == null || itemRequestDto.getDescription() == null) {
            throw new ValidationException("Передан пустой запрос");
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NoObjectException("Данный пользователь отсутствует в системе");
        }
        ItemRequest itemRequest = ItemRequestsMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userOptional.get());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestsMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(Integer userId) {
        List<ItemRequestDto> itemRequestDtos;
        if (!userRepository.existsById(userId)) {
            throw new NoObjectException("Данный пользователь отсутствует в системе");
        }
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(userId);
        itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(itemRequest);
            itemRequestDtos.add(itemRequestDto);
        }
        Map<Integer, ItemRequestDto> requests = itemRequestDtos.stream().collect(Collectors.toMap(ItemRequestDto::getId, Function.identity()));
        List<Item> items = itemRepository.findByRequestIdIn(new ArrayList<>(requests.keySet()));
        for (Item item : items) {
            OutputItemDto itemDto = ItemMapper.toOutputItemDto(item);
            requests.get(itemDto.getRequestId()).getItems().add(itemDto);
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer from, Integer size, Integer userId) {
        if (size < 1 || from < 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
        List<ItemRequestDto> itemRequestDtos;
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(from / size, size)).getContent();

        itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(itemRequest);
            itemRequestDtos.add(itemRequestDto);
        }
        Map<Integer, ItemRequestDto> requests = itemRequestDtos.stream().collect(Collectors.toMap(ItemRequestDto::getId, Function.identity()));
        List<Item> items = itemRepository.findByRequestIdIn(new ArrayList<>(requests.keySet()));
        for (Item item : items) {
            OutputItemDto itemDto = ItemMapper.toOutputItemDto(item);
            requests.get(itemDto.getRequestId()).getItems().add(itemDto);
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequest(Integer itemId, Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoObjectException("Пользователь отсутствует в системе");
        }
        Optional<ItemRequest> requestOptional = itemRequestRepository.findById(itemId);
        if (requestOptional.isEmpty()) {
            throw new NoObjectException("Данный запрос не найден в системе");
        } else {
            ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(requestOptional.get());
            itemRequestDto.setItems(ItemMapper.toOutputItemDtos(itemRepository.findByRequestId(itemRequestDto.getId())));
            return itemRequestDto;
        }
    }
}
