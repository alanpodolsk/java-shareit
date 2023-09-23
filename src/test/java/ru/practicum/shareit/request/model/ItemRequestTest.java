package ru.practicum.shareit.request.model;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {
    EasyRandom generator = new EasyRandom();

    @Test
    void testEquals() {
        ItemRequest request1 = generator.nextObject(ItemRequest.class);
        ItemRequest request2 = generator.nextObject(ItemRequest.class);
        Assertions.assertNotEquals(request2,request1);
        request2.setRequester(request1.getRequester());
        request2.setDescription(request1.getDescription());
        request2.setCreated(request1.getCreated());
        Assertions.assertEquals(request2,request1);
    }
}