package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestsMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoObjectException("Данный пользователь отсутствует в системе");
        } else {
            List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterId(userId);
            itemRequestDtos = new ArrayList<>();
            for (ItemRequest itemRequest : itemRequests) {
                ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(itemRequest);
                itemRequestDto.setItems(ItemMapper.toOutputItemDtos(itemRepository.findByRequestId(itemRequestDto.getId())));
                itemRequestDtos.add(itemRequestDto);
            }
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Integer from, Integer size, Integer userId) {
        List<ItemRequestDto> itemRequestDtos;
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(from, size)).getContent();
        itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(itemRequest);
            itemRequestDto.setItems(ItemMapper.toOutputItemDtos(itemRepository.findByRequestId(itemRequestDto.getId())));
            itemRequestDtos.add(itemRequestDto);
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequest(Integer itemId, Integer userId) {
        if(userRepository.findById(userId).isEmpty()){
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
