package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestServiceImplTest {
    EasyRandom generator = new EasyRandom();
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);




    @Test
    @DisplayName("Должен создать запрос")
    void shouldCreateRequest() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        requestDto.setId(null);
        requestDto.setItems(null);
        requestDto.setCreated(null);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ItemRequest.class));
        //Act
        ItemRequestDto savedRequestDto = itemRequestService.createRequest(requestDto, 1);
        Assertions.assertNotNull(savedRequestDto);
        Assertions.assertAll(
                () -> assertEquals(UserMapper.toUserDto(user), savedRequestDto.getRequester()),
                () -> assertNotNull(savedRequestDto.getCreated()));
    }

    @Test
    @DisplayName("Создание запроса - не найден пользователь")
    void shouldReturnUserNotFoundCreateRequest() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        requestDto.setId(null);
        requestDto.setItems(null);
        requestDto.setCreated(null);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ItemRequest.class));
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> itemRequestService.createRequest(requestDto, 4)
        );
        Assertions.assertEquals("Данный пользователь отсутствует в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Создание запроса - пустой запрос")
    void shouldReturnEmptyRequestCreateRequest() {
        //Act
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> itemRequestService.createRequest(null, 4)
        );
        Assertions.assertEquals("Передан пустой запрос", ex.getMessage());
    }

    @Test
    @DisplayName("Должен возвратить ошибку при некорректном пользователе")
    void shouldReturnNoObjectExceptionRequesterIsInvalid() {
        //Arrange
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        requestDto.setId(null);
        requestDto.setItems(null);
        requestDto.setCreated(null);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        Mockito.when(mockItemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ItemRequest.class));
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> itemRequestService.createRequest(requestDto, 100)
        );
        Assertions.assertEquals("Данный пользователь отсутствует в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть запросы, созданные пользователем")
    void getRequestsByUser() {
        //Arrange
        ItemRequest requestDto1 = generator.nextObject(ItemRequest.class);
        ItemRequest requestDto2 = generator.nextObject(ItemRequest.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockUserRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito.when(mockItemRequestRepository.findByRequesterId(Mockito.anyInt()))
                .thenReturn(List.of(requestDto1, requestDto2));
        //Act
        List<ItemRequestDto> savedRequests = itemRequestService.getRequestsByUser(1);
        //Assert
        Assertions.assertEquals(2, savedRequests.size());
    }

    @Test
    @DisplayName("Должен вернуть все запросы")
    void getAllRequests() {
        ItemRequest requestDto1 = generator.nextObject(ItemRequest.class);
        ItemRequest requestDto2 = generator.nextObject(ItemRequest.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRequestRepository.findAllByRequesterIdNot(Mockito.anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(requestDto1, requestDto2)));
        //
        List<ItemRequestDto> savedRequests = itemRequestService.getAllRequests(0, 50, 50);
        //Assert
        Assertions.assertEquals(2, savedRequests.size());
    }

    @Test
    @DisplayName("Должен вернуть все запросы - ошибка пагинации")
    void shouldReturnValidationExceptionPaginationGetAllRequests() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> itemRequestService.getAllRequests(-1, 2,1)
        );
        Assertions.assertEquals("Некорректные параметры пагинации", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть объект запроса")
    void shouldGetRequest() {
        ItemRequest requestDto = generator.nextObject(ItemRequest.class);
        Mockito.when(mockUserRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(requestDto));
        //Assert
        Assertions.assertNotNull(itemRequestService.getRequest(1, 1));
    }

    @Test
    @DisplayName("Должен вернуть объект запроса - объект не найден")
    void shouldReturnRequestNotFoundGetRequest() {
        Mockito.when(mockUserRepository.existsById(Mockito.anyInt()))
                .thenReturn(true);
        Mockito.when(mockItemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> itemRequestService.getRequest(1, 1)
        );
        Assertions.assertEquals("Данный запрос не найден в системе", ex.getMessage());
    }

    @Test
    @DisplayName("Должен вернуть объект запроса - объект не найден")
    void shouldReturnUserNotFoundGetRequest() {
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.empty());
        //Act
        NoObjectException ex = assertThrows(
                NoObjectException.class,
                () -> itemRequestService.getRequest(1, 1)
        );
        Assertions.assertEquals("Пользователь отсутствует в системе", ex.getMessage());
    }
}