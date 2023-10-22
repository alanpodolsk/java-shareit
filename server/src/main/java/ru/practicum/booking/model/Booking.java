package ru.practicum.booking.model;

import lombok.*;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotNull
    @Column(name = "start_date", nullable = false)
    LocalDateTime start;
    @NotNull
    @Column(name = "end_date", nullable = false)
    LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "booker_id")
    User booker;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(start, booking.start) && Objects.equals(end, booking.end) && Objects.equals(item, booking.item) && Objects.equals(booker, booking.booker) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, item, booker, status);
    }
}
