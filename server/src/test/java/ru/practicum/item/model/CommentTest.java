package ru.practicum.item.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.item.dto.CommentDto;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    EasyRandom generator = new EasyRandom();

    @Test
    void testEquals() {
        Comment comment1 = generator.nextObject(Comment.class);
        Comment comment2 = generator.nextObject(Comment.class);
        assertNotEquals(comment2,comment1);
        comment2.setItem(comment1.getItem());
        comment2.setAuthor(comment1.getAuthor());
        comment2.setText(comment1.getText());
        comment2.setCreated(comment1.getCreated());
        assertEquals(comment2,comment1);
        CommentDto comment3 = generator.nextObject(CommentDto.class);
        Assertions.assertNotEquals(comment3,comment1);
    }
}