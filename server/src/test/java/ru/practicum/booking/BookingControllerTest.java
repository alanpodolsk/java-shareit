package ru.practicum.booking;

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
import ru.practicum.booking.BookingController;
import ru.practicum.booking.BookingService;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoForCreate;
import ru.practicum.booking.dto.BookingMapper;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.exception.NoObjectException;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Должен создать бронирование")
    void shouldCreateBooking() throws Exception {
        BookingDtoForCreate booking = generator.nextObject(BookingDtoForCreate.class);
        User user = generator.nextObject(User.class);
        user.setId(1);
        Item item = generator.nextObject(Item.class);
        when(bookingService.createBooking(Mockito.any(BookingDtoForCreate.class),Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                            Booking booking1 = BookingMapper.toBooking(invocationOnMock.getArgument(0, BookingDtoForCreate.class));
                            booking1.setId(1);
                            booking1.setBooker(user);
                            booking1.setItem(item);
                            booking1.setStatus(BookingStatus.WAITING);
                            return BookingMapper.toBookingDto(booking1);
                        });
                mvc.perform(post("/bookings")
                                .content(mapper.writeValueAsString(booking))
                                .header("X-Sharer-User-Id",1)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(1),Integer.class))
                        .andExpect(jsonPath("$.item.id").value(item.getId()))
                        .andExpect(jsonPath("$.booker.id").value(user.getId()))
                        .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()));
    }

    @Test
    @DisplayName("Должен сменить статус пользователя")
    void setBookingStatus() throws Exception {
        BookingDto booking = generator.nextObject(BookingDto.class);
        booking.setId(1);
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.setBookingStatus(Mockito.anyInt(),Mockito.anyBoolean(),Mockito.anyInt()))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1),Integer.class))
                .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.toString()));
    }

    @Test
    @DisplayName("Должен создать бронирование")
    void shouldGetBooking() throws Exception {
            BookingDto booking = generator.nextObject(BookingDto.class);
            when(bookingService.getBooking(Mockito.anyInt(),Mockito.anyInt()))
                    .thenReturn(booking);
            mvc.perform(get("/bookings/1")
                            .header("X-Sharer-User-Id",1)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(booking.getId())))
                    .andExpect(jsonPath("$.item.id").value(booking.getItem().getId()))
                    .andExpect(jsonPath("$.booker.id").value(booking.getBooker().getId()))
                    .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));
        }

    @Test
    @DisplayName("Должен вернуть ошибку объект не найден")
    void shouldReturnNoObjectExceptionAfterGetBooking() throws Exception {
        when(bookingService.getBooking(Mockito.anyInt(),Mockito.anyInt()))
                .thenThrow(new NoObjectException("Объект не найден"));
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorType", is("Object not found")))
                .andExpect(jsonPath("$.error", is("Объект не найден")));
    }

    @Test
    @DisplayName("Должен вернуть список бронирований пользователя")
    void getBookingsByUser() throws Exception {
        BookingDto booking1 = generator.nextObject(BookingDto.class);
        BookingDto booking2 = generator.nextObject(BookingDto.class);
        when(bookingService.getBookingsByUser(Mockito.anyInt(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(List.of(booking1,booking2));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId())))
                .andExpect(jsonPath("$[0].item.id").value(booking1.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(booking1.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(booking1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id", is(booking2.getId())))
                .andExpect(jsonPath("$[1].item.id").value(booking2.getItem().getId()))
                .andExpect(jsonPath("$[1].booker.id").value(booking2.getBooker().getId()))
                .andExpect(jsonPath("$[1].status").value(booking2.getStatus().toString()));
    }

    @Test
    @DisplayName("Должен вернуть ошибку проверки State при запросе списка бронирований")
    void shouldReturnValidationErrorAfterGetBookingsByUser() throws Exception {
        when(bookingService.getBookingsByUser(Mockito.anyInt(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
                .thenThrow(new IllegalArgumentException("Тестовая ошибка"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorType", is("IllegalArgumentException")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }

    @Test
    @DisplayName("Должен вернуть ошибку runtime при запросе списка бронирований")
    void shouldReturnRuntimeErrorAfterGetBookingsByUser() throws Exception {
        when(bookingService.getBookingsByUser(Mockito.anyInt(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
                .thenThrow(new RuntimeException("Тестовая ошибка"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorType", is("Runtime error")))
                .andExpect(jsonPath("$.error", is("Тестовая ошибка")));
    }


    @Test
    @DisplayName("Должен вернуть список бронирований пользователя")
    void shouldGetBookingsByUsersItems() throws Exception {
        BookingDto booking1 = generator.nextObject(BookingDto.class);
        BookingDto booking2 = generator.nextObject(BookingDto.class);
        when(bookingService.getBookingsByUsersItems(Mockito.anyInt(),Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt()))
                .thenReturn(List.of(booking1,booking2));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id",1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(booking1.getId())))
                .andExpect(jsonPath("$[0].item.id").value(booking1.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(booking1.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(booking1.getStatus().toString()))
                .andExpect(jsonPath("$[1].id", is(booking2.getId())))
                .andExpect(jsonPath("$[1].item.id").value(booking2.getItem().getId()))
                .andExpect(jsonPath("$[1].booker.id").value(booking2.getBooker().getId()))
                .andExpect(jsonPath("$[1].status").value(booking2.getStatus().toString()));
    }
}