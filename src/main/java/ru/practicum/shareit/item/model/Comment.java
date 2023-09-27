package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false, length = 1000)
    String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @Column(nullable = false)
    LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(text, comment.text) && Objects.equals(author, comment.author) && Objects.equals(item, comment.item) && Objects.equals(created, comment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, author, item, created);
    }
}
