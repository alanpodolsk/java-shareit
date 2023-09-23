package ru.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItemIdIn(List<Integer> itemId);

    List<Comment> findByItemId(Integer itemId);
}
