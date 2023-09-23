package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Должен создать вещь")
    void shouldCreateItem() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setId(1);
        InputItemDto item = generator.nextObject(InputItemDto.class);
        when(itemService.createItem(Mockito.any(InputItemDto.class),Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    OutputItemDto itemDto = ItemMapper.toOutputItemDto(ItemMapper.toItem(invocationOnMock.getArgument(0, InputItemDto.class)));
                    itemDto.setId(1);
                    itemDto.setOwner(user);
                    return itemDto;
                });
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.owner").value(user));
    }

    @Test
    @DisplayName("Должен вернуть тестовую ошибку Bad Request")
    void createItemShouldReturnBadRequest() throws Exception {
        User user = generator.nextObject(User.class);
        user.setId(1);
        InputItemDto item = generator.nextObject(InputItemDto.class);
        when(itemService.createItem(Mockito.any(InputItemDto.class),Mockito.anyInt()))
                .thenThrow(new ValidationException("Тестовая ошибка"));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("Validation error")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }

    @Test
    @DisplayName("Должен вернуть тестовую ошибку Not Found")
    void createItemShouldReturnNotFound() throws Exception {
        User user = generator.nextObject(User.class);
        user.setId(1);
        InputItemDto item = generator.nextObject(InputItemDto.class);
        when(itemService.createItem(Mockito.any(InputItemDto.class),Mockito.anyInt()))
                .thenThrow(new NoObjectException("Тестовая ошибка"));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorType", is("Object not found")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }

    @Test
    @DisplayName("Должен вернуть тестовую ошибку Conflict")
    void createItemShouldReturnConflict() throws Exception {
        User user = generator.nextObject(User.class);
        user.setId(1);
        InputItemDto item = generator.nextObject(InputItemDto.class);
        when(itemService.createItem(Mockito.any(InputItemDto.class),Mockito.anyInt()))
                .thenThrow(new ConflictException("Тестовая ошибка"));
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorType", is("Conflict error")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }

    @Test
    @DisplayName("Должен создать комментарий")
    void createComment() throws Exception {
        User user = generator.nextObject(User.class);
        user.setId(1);
        OutputItemDto item = generator.nextObject(OutputItemDto.class);
        Comment comment = generator.nextObject(Comment.class);
        when(itemService.createComment(Mockito.any(Comment.class),Mockito.anyInt(),Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    CommentDto commentDto = ItemMapper.toCommentDto(invocationOnMock.getArgument(0, Comment.class));
                    commentDto.setAuthorName(user.getName());
                    commentDto.setItemId(item.getId());
                    return commentDto;
                });
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.itemId").value(item.getId()))
                .andExpect(jsonPath("$.authorName").value(user.getName()));
    }

    @Test
    @DisplayName("Должен обновить вещь")
    void shouldUpdateItem() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setId(1);
        InputItemDto item = generator.nextObject(InputItemDto.class);
        when(itemService.updateItem(Mockito.any(InputItemDto.class),Mockito.anyInt(),Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    OutputItemDto itemDto = ItemMapper.toOutputItemDto(ItemMapper.toItem(invocationOnMock.getArgument(0, InputItemDto.class)));
                    itemDto.setId(1);
                    itemDto.setOwner(user);
                    return itemDto;
                });
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.owner.id").value(user.getId()));
    }

    @Test
    @DisplayName("Должен вернуть вещь по id")
    void shouldGetItem() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setId(1);
        OutputItemDto item = generator.nextObject(OutputItemDto.class);
        item.setOwner(user);
        when(itemService.getItem(Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(item);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description").value(item.getDescription()))
                .andExpect(jsonPath("$.available").value(item.getAvailable()))
                .andExpect(jsonPath("$.owner").value(user));
    }

    @Test
    @DisplayName("Должен вернуть вещи пользователя")
    void getItemsByUser() throws Exception {
        //Arrange
        UserDto user = generator.nextObject(UserDto.class);
        user.setId(1);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        item1.setOwner(user);
        item2.setOwner(user);
        when(itemService.getItemsByUser(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(List.of(item1,item2));
        //Act
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
        //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId())))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[0].owner").value(user))
                .andExpect(jsonPath("$[1].id", is(item2.getId())))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[1].owner").value(user));
    }

    @Test
    @DisplayName("Должен вернуть вещи по поиску")
    void getItemsBySearch() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setId(1);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        item1.setOwner(user);
        item2.setOwner(user);
        when(itemService.getItemsBySearch(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(List.of(item1,item2));
        mvc.perform(get("/items/search?text=дРелЬ")
                        .header("X-Sharer-User-Id",3)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(item1.getId())))
                .andExpect(jsonPath("$[0].name", is(item1.getName())))
                .andExpect(jsonPath("$[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[0].owner").value(user))
                .andExpect(jsonPath("$[1].id", is(item2.getId())))
                .andExpect(jsonPath("$[1].name", is(item2.getName())))
                .andExpect(jsonPath("$[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[1].owner").value(user));
    }
}