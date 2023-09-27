package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBookerId(Integer bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndItemIdAndStatusAndStartIsBefore(Integer bookerId, Integer itemId, BookingStatus status, LocalDateTime checkTime);

    Page<Booking> findByBookerIdAndStatus(Integer bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemIdIn(List<Integer> itemIds, Pageable pageable);

    Page<Booking> findByItemIdInAndStatus(List<Integer> itemIds, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemIdInAndStartIsAfter(List<Integer> itemIds, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemIdInAndEndIsBefore(List<Integer> itemIds, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Integer> itemIds, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "select * from bookings where item_id in (?1) AND start_date = (SELECT MAX(start_date) from bookings where item_id in (?1) AND start_date < now() AND status != 'REJECTED')", nativeQuery = true)
    List<Booking> findLastBookings(List<Integer> itemIds);

    @Query(value = "select * from bookings where item_id in (?1) AND start_date = (SELECT MIN(start_date) from bookings where item_id in (?1) AND start_date > now() AND status != 'REJECTED')", nativeQuery = true)
    List<Booking> findNextBookings(List<Integer> itemIds);


    @Query(value = "select * from bookings where item_id = ?1 AND start_date = (SELECT MAX(start_date) from bookings where item_id = ?1 AND start_date < now() AND status != 'REJECTED')", nativeQuery = true)
    Optional<Booking> findLastBooking(Integer itemId);

    @Query(value = "select * from bookings where item_id = ?1 AND start_date = (SELECT MIN(start_date) from bookings where item_id = ?1 AND start_date > now() AND status != 'REJECTED')", nativeQuery = true)
    Optional<Booking> findNextBooking(Integer itemId);
}
