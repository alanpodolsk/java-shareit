package ru.practicum.booking.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {
    EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Проверка equals and hashcode")
    void testEquals() {
        Booking booking1 = generator.nextObject(Booking.class);
        Booking booking2 = generator.nextObject(Booking.class);
        assertNotEquals(booking2, booking1);
        booking2.setId(booking1.getId());
        booking2.setItem(booking1.getItem());
        booking2.setStatus(booking1.getStatus());
        booking2.setBooker(booking1.getBooker());
        booking2.setStart(booking1.getStart());
        booking2.setEnd(booking1.getEnd());
        assertEquals(booking2, booking1);
        booking2.setId(7);
        assertEquals(booking2, booking1);
    }
}