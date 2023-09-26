package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoForCreate;

import java.util.List;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public BookingDto createBooking(BookingDtoForCreate bookingDto, Integer userId) {
        return null;
    }


    public BookingDto setBookingStatus(Integer bookingId, boolean approved, Integer userId) {
        return null;
    }


    public BookingDto getBooking(Integer bookingId, Integer userId) {
        return null;
    }


    public List<BookingDto> getBookingsByUser(Integer userId, String state, Integer from, Integer size) {
        return null;
    }


    public List<BookingDto> getBookingByUsersItems(Integer userId, String state, Integer from, Integer size) {
        return null;
    }
}
