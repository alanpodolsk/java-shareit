package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerId(Integer bookerId, Sort sort);

    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(Integer bookerId, Integer itemId, BookingStatus status, LocalDateTime checkTime);

    List<Booking> findByBookerIdAndStatus(Integer bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemIdIn(List<Integer> itemIds, Sort start);

    List<Booking> findByItemIdInAndStatus(List<Integer> itemIds, BookingStatus status, Sort sort);

    List<Booking> findByItemIdInAndStartIsAfter(List<Integer> itemIds, LocalDateTime start, Sort sort);

    List<Booking> findByItemIdInAndEndIsBefore(List<Integer> itemIds, LocalDateTime start, Sort sort);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Integer> itemIds, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query(value = "select * from bookings where item_id = ?1 AND status != 'REJECTED' AND start_date = (SELECT MAX(start_date) from bookings where item_id = ?1 AND start_date < now())", nativeQuery = true)
    Optional<Booking> findLastBooking(Integer itemId);

    @Query(value = "select * from bookings where item_id = ?1 AND status != 'REJECTED' AND start_date = (SELECT MIN(start_date) from bookings where item_id = ?1 AND start_date > now())", nativeQuery = true)
    Optional<Booking> findNextBooking(Integer itemId);
}
