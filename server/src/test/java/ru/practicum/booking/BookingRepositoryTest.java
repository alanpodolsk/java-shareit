package ru.practicum.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.booking.model.Booking;
import ru.practicum.booking.model.BookingStatus;
import ru.practicum.item.ItemRepository;
import ru.practicum.item.model.Item;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    EasyRandom generator = new EasyRandom();
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Должен вернуть последнее бронирование N2")
    void shouldFindLastBooking() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().minusMinutes(15),LocalDateTime.now().minusMinutes(5),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,LocalDateTime.now().minusMinutes(10),LocalDateTime.now().minusMinutes(1),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user2, BookingStatus.REJECTED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> lastBooking = bookingRepository.findLastBooking(1);
        //Assert
        Assertions.assertTrue(lastBooking.isPresent());
        Assertions.assertEquals(booking2,lastBooking.get());
    }

    @Test
    @DisplayName("Должен вернуть пустое последнее бронирование - все отклонены")
    void shouldFindEmptyLastBookingAllRejected() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().minusMinutes(15),LocalDateTime.now().minusMinutes(5),savedItem1,user1, BookingStatus.REJECTED);
        Booking booking2 = new Booking(null,LocalDateTime.now().minusMinutes(10),LocalDateTime.now().minusMinutes(1),savedItem1,user1, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user2, BookingStatus.REJECTED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> lastBooking = bookingRepository.findLastBooking(1);
        //Assert
        Assertions.assertTrue(lastBooking.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустое последнее бронирование - все в будущем")
    void shouldFindEmptyLastBookingAllInFuture() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,LocalDateTime.now().plusMinutes(27),LocalDateTime.now().plusMinutes(47),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user2, BookingStatus.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> lastBooking = bookingRepository.findLastBooking(1);
        //Assert
        Assertions.assertTrue(lastBooking.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть следующее бронирование N3")
    void shouldFindNextBooking() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().minusMinutes(15),LocalDateTime.now().minusMinutes(5),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,LocalDateTime.now().plusMinutes(10),LocalDateTime.now().plusMinutes(14),savedItem1,user1, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user2, BookingStatus.WAITING);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> nextBooking = bookingRepository.findNextBooking(savedItem1.getId());
        //Assert
        Assertions.assertTrue(nextBooking.isPresent());
        Assertions.assertEquals(booking3,nextBooking.get());
    }

    @Test
    @DisplayName("Должен вернуть пустое следующее бронирование - все отклонены")
    void shouldFindEmptyNextBookingAllRejected() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);
        user3 = userRepository.save(user3);

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().minusMinutes(15),LocalDateTime.now().minusMinutes(5),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,LocalDateTime.now().plusMinutes(10),LocalDateTime.now().plusMinutes(14),savedItem1,user1, BookingStatus.REJECTED);
        Booking booking3 = new Booking(null,LocalDateTime.now().plusMinutes(15),LocalDateTime.now().plusMinutes(25),savedItem1,user2, BookingStatus.REJECTED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> nextBooking = bookingRepository.findNextBooking(savedItem1.getId());
        //Assert
        Assertions.assertTrue(nextBooking.isEmpty());
    }

    @Test
    @DisplayName("Должен вернуть пустое следующее бронирование - все в прошлом")
    void shouldFindEmptyNextBookingAllInPast() {
        User user1 = new User(null,"Вася","vasya@mail.ru");
        User user2 = new User(null,"Василий","vasya1@mail.ru");
        User user3 = new User(null,"Василий1","vasya11@mail.ru");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        Assertions.assertNotNull(entityManager.find(User.class,1));

        Item item1 = generator.nextObject(Item.class);
        Item item2 = generator.nextObject(Item.class);
        item1.setOwner(user3);
        item1.setRequest(null);
        item2.setOwner(user3);
        item2.setRequest(null);

        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        Booking booking1 = new Booking(null,LocalDateTime.now().minusMinutes(15),LocalDateTime.now().minusMinutes(5),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking2 = new Booking(null,LocalDateTime.now().minusMinutes(13),LocalDateTime.now().minusMinutes(2),savedItem1,user1, BookingStatus.APPROVED);
        Booking booking3 = new Booking(null,LocalDateTime.now().minusMinutes(8),LocalDateTime.now().minusMinutes(1),savedItem1,user2, BookingStatus.APPROVED);

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        Assertions.assertNotNull(entityManager.find(Booking.class,1));
        Assertions.assertNotNull(entityManager.find(Booking.class,2));
        Assertions.assertNotNull(entityManager.find(Booking.class,3));

        //Act

        Optional<Booking> nextBooking = bookingRepository.findNextBooking(savedItem1.getId());
        //Assert
        Assertions.assertTrue(nextBooking.isEmpty());
    }
}