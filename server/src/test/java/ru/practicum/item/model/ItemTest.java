package ru.practicum.item.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {
    EasyRandom generator = new EasyRandom();

    @Test
    void testEquals() {
        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        assertNotEquals(item1,item2);
        item2.setName(item1.getName());
        item2.setDescription(item1.getDescription());
        item2.setAvailable(item1.getAvailable());
        item2.setOwner(item1.getOwner());
        item2.setRequest(item1.getRequest());
        assertEquals(item1,item2);
    }
}