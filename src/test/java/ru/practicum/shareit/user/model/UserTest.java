package ru.practicum.shareit.user.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    EasyRandom generator = new EasyRandom();

    @Test
    @DisplayName("Проверка equals and hashcode")
    void testEquals() {
        User user1 = generator.nextObject(User.class);
        User user2 = generator.nextObject(User.class);
        assertNotEquals(user1, user2);
        user2.setName(user1.getName());
        user2.setEmail(user1.getEmail());
        assertEquals(user1, user2);
        user2.setId(user1.getId());
        assertEquals(user1, user2);
    }
}