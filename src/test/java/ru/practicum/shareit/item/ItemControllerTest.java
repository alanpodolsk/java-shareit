package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutputItemDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

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
    @DisplayName("Должен создать пользователя")
    void shouldCreateItem() throws Exception {
        User user = generator.nextObject(User.class);
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
    void createComment() {
    }

    @Test
    void shouldUpdateItem() throws Exception {
        User user = generator.nextObject(User.class);
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
                .andExpect(jsonPath("$.owner").value(user));
    }

    @Test
    void shouldGetItem() throws Exception {
        User user = generator.nextObject(User.class);
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
    void getItemsByUser() {
    }

    @Test
    void getItemsBySearch() {
    }
}