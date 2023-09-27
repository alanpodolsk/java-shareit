package ru.practicum.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoForCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody BookingDtoForCreate bookingDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setBookingStatus(@PathVariable Integer bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.setBookingStatus(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Integer bookingId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state, @RequestParam (defaultValue = "0") Integer from, @RequestParam (defaultValue = "50") Integer size) {
        return bookingClient.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByUsersItems(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state, @RequestParam (defaultValue = "0") Integer from, @RequestParam (defaultValue = "50") Integer size) {
        return bookingClient.getBookingsByUsersItems(userId, state, from, size);
    }
}
