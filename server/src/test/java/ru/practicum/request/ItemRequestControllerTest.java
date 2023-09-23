package ru.practicum.request;

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
import ru.practicum.exception.NoObjectException;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Должен создать запрос")
    void shouldCreateRequest() throws Exception {
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        requestDto.setItems(Collections.emptyList());
        when(itemRequestService.createRequest(Mockito.any(ItemRequestDto.class), Mockito.anyInt()))
                .thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requester.id").value(requestDto.getRequester().getId()));
    }

    @Test
    @DisplayName("Должен вернуть перечень запросов пользователя")
    void getRequestsByUser() throws Exception {
        ItemRequestDto requestDto1 = generator.nextObject(ItemRequestDto.class);
        ItemRequestDto requestDto2 = generator.nextObject(ItemRequestDto.class);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        requestDto1.setItems(List.of(item1, item2));
        requestDto2.setItems(List.of(item1, item2));
        when(itemRequestService.getRequestsByUser(Mockito.anyInt()))
                .thenReturn(List.of(requestDto1, requestDto2));
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$[0].items.[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].items.[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[0].items.[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].items.[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[0].items.[0].owner").value(item1.getOwner()))
                .andExpect(jsonPath("$[0].items.[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[0].items.[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[0].items.[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[0].items.[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[0].items.[1].owner").value(item2.getOwner()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].requester.id").value(requestDto2.getRequester().getId()))
                .andExpect(jsonPath("$[1].items.[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[1].items.[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].items.[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[1].items.[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[1].items.[0].owner").value(item1.getOwner()))
                .andExpect(jsonPath("$[1].items.[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].items.[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[1].items.[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].items.[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[1].items.[1].owner").value(item2.getOwner()));
    }

    @Test
    @DisplayName("Должен вернуть перечень всех запросов")
    void shouldGetAllRequests() throws Exception {
        ItemRequestDto requestDto1 = generator.nextObject(ItemRequestDto.class);
        ItemRequestDto requestDto2 = generator.nextObject(ItemRequestDto.class);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        requestDto1.setItems(List.of(item1, item2));
        requestDto2.setItems(List.of(item1, item2));
        when(itemRequestService.getAllRequests(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(requestDto1, requestDto2));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto1.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto1.getDescription()))
                .andExpect(jsonPath("$[0].requester.id").value(requestDto1.getRequester().getId()))
                .andExpect(jsonPath("$[0].items.[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[0].items.[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[0].items.[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[0].items.[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[0].items.[0].owner").value(item1.getOwner()))
                .andExpect(jsonPath("$[0].items.[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[0].items.[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[0].items.[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[0].items.[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[0].items.[1].owner").value(item2.getOwner()))
                .andExpect(jsonPath("$[1].id").value(requestDto2.getId()))
                .andExpect(jsonPath("$[1].description").value(requestDto2.getDescription()))
                .andExpect(jsonPath("$[1].requester.id").value(requestDto2.getRequester().getId()))
                .andExpect(jsonPath("$[1].items.[0].id").value(item1.getId()))
                .andExpect(jsonPath("$[1].items.[0].name").value(item1.getName()))
                .andExpect(jsonPath("$[1].items.[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$[1].items.[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$[1].items.[0].owner").value(item1.getOwner()))
                .andExpect(jsonPath("$[1].items.[1].id").value(item2.getId()))
                .andExpect(jsonPath("$[1].items.[1].name").value(item2.getName()))
                .andExpect(jsonPath("$[1].items.[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$[1].items.[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$[1].items.[1].owner").value(item2.getOwner()));
    }

    @Test
    @DisplayName("Должен вернуть запрос")
    void shouldGetRequest() throws Exception {
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        requestDto.setItems(List.of(item1, item2));
        when(itemRequestService.getRequest(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(requestDto);
        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requester.id").value(requestDto.getRequester().getId()))
                .andExpect(jsonPath("$.items.[0].id").value(item1.getId()))
                .andExpect(jsonPath("$.items.[0].name").value(item1.getName()))
                .andExpect(jsonPath("$.items.[0].description").value(item1.getDescription()))
                .andExpect(jsonPath("$.items.[0].available").value(item1.getAvailable()))
                .andExpect(jsonPath("$.items.[0].owner").value(item1.getOwner()))
                .andExpect(jsonPath("$.items.[1].id").value(item2.getId()))
                .andExpect(jsonPath("$.items.[1].name").value(item2.getName()))
                .andExpect(jsonPath("$.items.[1].description").value(item2.getDescription()))
                .andExpect(jsonPath("$.items.[1].available").value(item2.getAvailable()))
                .andExpect(jsonPath("$.items.[1].owner").value(item2.getOwner()));
    }

    @Test
    @DisplayName("Должен вернуть ошибку Not found")
    void shouldReturnNotFoundExceptionAfterGetRequest() throws Exception {
        ItemRequestDto requestDto = generator.nextObject(ItemRequestDto.class);
        OutputItemDto item1 = generator.nextObject(OutputItemDto.class);
        OutputItemDto item2 = generator.nextObject(OutputItemDto.class);
        requestDto.setItems(List.of(item1, item2));
        when(itemRequestService.getRequest(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new NoObjectException("Запрос не найден"));
        mvc.perform(get("/requests/1")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorType", is("Object not found")))
                .andExpect(jsonPath("$.error", is("Запрос не найден")));
    }
}