package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {
    EasyRandom generator = new EasyRandom();
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Должен вернуть предметы 1 и 3 - hammer")
    void shouldReturnItems1And3Hammer() {
        //Arrange
        User user1 = new User(null, "Вася", "vasya@mail.ru");

        user1 = userRepository.save(user1);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        Item item3 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setRequest(null);
        item1.setName("Modern HaMMer");
        item2.setOwner(user1);
        item2.setRequest(null);
        item3.setOwner(user1);
        item3.setRequest(null);
        item3.setDescription("Super tools include hammer");

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);
        Item savedItem3 = itemRepository.save(item3);

        //Act
        List<Item> items = itemRepository.search("Hammer", PageRequest.of(0, 50)).getContent();
        //Assert
        Assertions.assertTrue(items.size() == 2);
        Assertions.assertEquals(savedItem1.getId(), items.get(0).getId());
        Assertions.assertEquals(savedItem3.getId(), items.get(1).getId());
    }

    @Test
    @DisplayName("Должен не вернуть ничего - нет совпадений")
    void shouldReturnNoneItemsNoMatches() {
        //Arrange
        User user1 = new User(null, "Вася", "vasya@mail.ru");

        user1 = userRepository.save(user1);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        Item item3 = generator.nextObject(Item.class);
        item1.setOwner(user1);
        item1.setRequest(null);
        item1.setName("Modern HaMMer");
        item2.setOwner(user1);
        item2.setRequest(null);
        item3.setOwner(user1);
        item3.setRequest(null);
        item3.setDescription("Super tools include hammer");

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);
        Item savedItem3 = itemRepository.save(item3);

        //Act
        List<Item> items = itemRepository.search("Beretta", PageRequest.of(0, 50)).getContent();
        //Assert
        Assertions.assertTrue(items.size() == 0);
    }
}