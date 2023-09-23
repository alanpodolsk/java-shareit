package ru.practicum.user;

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
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Должен вернуть пользователя")
    void shouldGetUser() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        when(userService.getUser(Mockito.anyInt()))
                .thenReturn(user);
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Должен вернуть ошибку Not Found")
    void shouldReturnNoObjectExceptionAfterGetUser() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        when(userService.getUser(Mockito.anyInt()))
                .thenThrow(new NoObjectException("Пользователь не найден"));
        mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorType", is("Object not found")))
                .andExpect(jsonPath("$.error", is("Пользователь не найден")));
    }

    @Test
    @DisplayName("Должен вернуть список пользователей")
    void getAllUsers() throws Exception {
        UserDto user1 = generator.nextObject(UserDto.class);
        UserDto user2 = generator.nextObject(UserDto.class);
        when(userService.getAllUsers())
                .thenReturn(List.of(user1, user2));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(user1.getId())))
                .andExpect(jsonPath("$[0].name").value(user1.getName()))
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(jsonPath("$[1].id", is(user2.getId())))
                .andExpect(jsonPath("$[1].name").value(user2.getName()))
                .andExpect(jsonPath("$[1].email").value(user2.getEmail()));
    }

    @Test
    @DisplayName("Должен обновить пользователя")
    void shouldUpdateUser() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        when(userService.updateUser((Mockito.any(UserDto.class)), Mockito.anyInt()))
                .thenReturn(user);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Должен вернуть ошибку Validation Error")
    void shouldReturnValidationErrorAfterUpdateUser() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        when(userService.updateUser((Mockito.any(UserDto.class)), Mockito.anyInt()))
                .thenThrow(new ValidationException("Тестовая ошибка"));
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("Validation error")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }

    @Test
    @DisplayName("Должен создать пользователя")
    void shouldCreateUser() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setEmail("email@email.ru");
        when(userService.createUser((Mockito.any(UserDto.class))))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId())))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    @DisplayName("Должен вернуть статус 400 - ошибка валидации Email")
    void shouldReturnBadRequestWhenEmailIsIncorrect() throws Exception {
        UserDto user = generator.nextObject(UserDto.class);
        user.setEmail("email.ru");
        when(userService.createUser((Mockito.any(UserDto.class))))
                .thenReturn(user);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Должен удалить пользователя и вернуть статус 200")
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}