package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoForCreate bookingDto, @RequestHeader("X-Sharer-User-Id") Integer userId){
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(@PathVariable Integer bookingId, @RequestParam boolean approved, @RequestHeader("X-Sharer-User-Id") Integer userId){
        return bookingService.setBookingStatus(bookingId,approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Integer bookingId, @RequestHeader("X-Sharer-User-Id") Integer userId){
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state){
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByUsersItems (@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestParam(defaultValue = "ALL") String state){
        return bookingService.getBookingsByUsersItems(userId, state);
    }
}
