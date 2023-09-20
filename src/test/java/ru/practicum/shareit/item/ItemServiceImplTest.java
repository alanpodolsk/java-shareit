package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceImplTest {
    EasyRandom generator = new EasyRandom();
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);
    ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    ItemServiceImpl itemService = new ItemServiceImpl(mockUserRepository,mockItemRepository,mockBookingRepository,mockCommentRepository, mockItemRequestRepository);


    @Test
    @DisplayName("Должен создать Item")
    void shouldCreateItem() {
        //Arrange
        User user = generator.nextObject(User.class);
        InputItemDto inputItem = generator.nextObject(InputItemDto.class);
        inputItem.setRequestId(null);
        Item outputItem = generator.nextObject(Item.class);
        outputItem.setName(inputItem.getName());
        outputItem.setDescription(inputItem.getDescription());
        outputItem.setAvailable(inputItem.getAvailable());
        outputItem.setId(1);

        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));

        //Act
        OutputItemDto savedItemDto = itemService.createItem(inputItem,1);
        //Assert
        Assertions.assertNotNull(savedItemDto);
        assertEquals(user,savedItemDto.getOwner());
    }

    @Test
    @DisplayName("Должен обновить объект Item")
    void shouldUpdateItem() {
        //Arrange
        User user = generator.nextObject(User.class);
        user.setId(1);
        InputItemDto inputItem = generator.nextObject(InputItemDto.class);
        inputItem.setRequestId(null);
        inputItem.setId(1);
        Item outputItem = generator.nextObject(Item.class);
        outputItem.setId(1);
        outputItem.setOwner(user);

        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(outputItem));
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));

        //Act
        OutputItemDto savedItemDto = itemService.updateItem(inputItem,1,1);
        //Assert
        Assertions.assertNotNull(savedItemDto);
        Assertions.assertAll(
                ()-> assertEquals(inputItem.getName(),savedItemDto.getName()),
                ()-> assertEquals(inputItem.getDescription(),savedItemDto.getDescription()),
                ()-> assertEquals(inputItem.getAvailable(),savedItemDto.getAvailable()));
    }

    @Test
    @DisplayName("Должен вернуть объект Item без бронирований")
    void shouldGetItemWithoutBookings() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item outputItem = generator.nextObject(Item.class);
        outputItem.setId(7);
        outputItem.setOwner(user);
        Booking lastBooking = generator.nextObject(Booking.class);
        Booking nextBooking = generator.nextObject(Booking.class);

        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(outputItem));
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito.when(mockBookingRepository.findLastBooking(Mockito.anyInt()))
                .thenReturn(Optional.of(lastBooking));
        Mockito.when(mockBookingRepository.findNextBooking(Mockito.anyInt()))
                .thenReturn(Optional.of(nextBooking));
        //Act
        OutputItemDto savedItemDto = itemService.getItem(7,3);
        //Assert
        Assertions.assertNotNull(savedItemDto);
        Assertions.assertAll(
                ()-> assertNull(savedItemDto.getLastBooking()),
                ()-> assertNull(savedItemDto.getNextBooking()));
    }

    @Test
    @DisplayName("Должен вернуть объект Item c бронированиями")
    void shouldGetItemWithBookings() {
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item outputItem = generator.nextObject(Item.class);
        outputItem.setId(7);
        outputItem.setOwner(user);
        Booking lastBooking = generator.nextObject(Booking.class);
        Booking nextBooking = generator.nextObject(Booking.class);

        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(outputItem));
        Mockito.when(mockItemRepository.save(Mockito.any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito.when(mockBookingRepository.findLastBooking(Mockito.anyInt()))
                .thenReturn(Optional.of(lastBooking));
        Mockito.when(mockBookingRepository.findNextBooking(Mockito.anyInt()))
                .thenReturn(Optional.of(nextBooking));
        //Act
        OutputItemDto savedItemDto = itemService.getItem(7,1);
        //Assert
        Assertions.assertNotNull(savedItemDto);
        Assertions.assertAll(
                ()-> assertEquals(savedItemDto.getLastBooking(), BookingMapper.toBookingDtoForItemList(lastBooking)),
                ()-> assertEquals(savedItemDto.getNextBooking(), BookingMapper.toBookingDtoForItemList(nextBooking)));
    }

    @Test
    @DisplayName("Должен вернуть объекты пользователя")
    void shouldGetItemsByUser() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findByOwnerIdOrderByIdAsc(Mockito.anyInt(),Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1,item2)));
        //Act
        List<OutputItemDto> savedItems = itemService.getItemsByUser(1,0,50);
        //Assert
        Assertions.assertEquals(savedItems.size(),List.of(item1,item2).size());
    }

    @Test
    @DisplayName("Должен вернуть объекты по поиску")
    void shouldGetItemsBySearch() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.search(Mockito.anyString(),Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1,item2)));
        //Act
        List<OutputItemDto> savedItems = itemService.getItemsBySearch("any",0,50);
        //Assert
        Assertions.assertEquals(savedItems.size(),List.of(item1,item2).size());
    }

    @Test
    @DisplayName("Должен вернуть пустой список при пустом поиске")
    void shouldGetItemsByEmptySearch() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.search(Mockito.anyString(),Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1,item2)));
        //Act
        List<OutputItemDto> savedItems = itemService.getItemsBySearch("",0,50);
        //Assert
        Assertions.assertEquals(savedItems.size(),0);
    }

    @Test
    @DisplayName("Должен создать комментарий")
    void shouldCreateComment() {
        //Arrange
        Item item = generator.nextObject(Item.class);
        User user = generator.nextObject(User.class);
        Comment comment = generator.nextObject(Comment.class);
        Booking booking = generator.nextObject(Booking.class);
        user.setId(1);
        item.setId(1);
        Mockito.when(mockUserRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(mockCommentRepository.save(Mockito.any(Comment.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Comment.class));
        Mockito.when(mockBookingRepository.findByBookerIdAndItemIdAndStatusAndStartIsBefore(Mockito.anyInt(),Mockito.anyInt(), Mockito.any(BookingStatus.class),Mockito.any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        //Act
        CommentDto savedComment = itemService.createComment(comment,1,1);
        //Assert
        Assertions.assertNotNull(savedComment);
        Assertions.assertAll(
                ()-> assertEquals(savedComment.getAuthorName(), user.getName()),
                ()-> assertEquals(savedComment.getItem(), item));
    }
}