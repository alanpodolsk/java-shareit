package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    @Query(value = "select * from comments where item_id = ?1", nativeQuery = true)
    List<Comment> findAllByItemId(Integer itemId);
}
