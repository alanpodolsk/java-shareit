package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.booking.dto.BookingDtoForCreate;

import java.util.Map;

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

    public ResponseEntity<Object> createBooking(BookingDtoForCreate bookingDto, Integer userId) {
        BookingValidations.validateCreateBooking(bookingDto, userId);
        return post("", userId, bookingDto);
    }


    public ResponseEntity<Object> setBookingStatus(Integer bookingId, boolean approved, Integer userId) {
        BookingValidations.validateSetBookingStatus(bookingId, approved, userId);
        return patch("/" + bookingId + "/?approved={approved}", userId, Map.of("approved", approved));
    }


    public ResponseEntity<Object> getBooking(Integer bookingId, Integer userId) {
        return get("/" + bookingId, userId);
    }


    public ResponseEntity<Object> getBookingsByUser(Integer userId, String state, Integer from, Integer size) {
        BookingValidations.validateGetBookings(userId, state, from, size);
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("?state={state}&from={from}&size={size}", Long.valueOf(userId), parameters);
    }


    public ResponseEntity<Object> getBookingsByUsersItems(Integer userId, String state, Integer from, Integer size) {
        BookingValidations.validateGetBookings(userId, state, from, size);
        Map<String, Object> parameters = Map.of("state", state, "from", from, "size", size);
        return get("/owner?state={state}&from={from}&size={size}", Long.valueOf(userId), parameters);
    }
}
