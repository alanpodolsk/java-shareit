package ru.practicum.request.model;

import lombok.*;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @NotBlank
    @Column(nullable = false, length = 1000)
    String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Column(nullable = false)
    LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest that = (ItemRequest) o;
        return Objects.equals(description, that.description) && Objects.equals(requester, that.requester) && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, requester, created);
    }
}
