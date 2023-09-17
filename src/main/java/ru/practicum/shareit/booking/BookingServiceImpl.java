package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateStatus;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(BookingDtoForCreate bookingDto, Integer userId) {
        if (bookingDto == null) {
            throw new NoObjectException("Не передан объект бронирования");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoObjectException("Пользователь не найден в системе");
        }
        Optional<Item> itemOpt = itemRepository.findById(bookingDto.getItemId());
        if (itemOpt.isEmpty()) {
            throw new NoObjectException("Данного объекта нет в БД");
        } else if (itemOpt.get().getAvailable() == Boolean.FALSE) {
            throw new ValidationException("Объект недоступен для бронирования");
        } else if (itemOpt.get().getOwner().getId().equals(userId)) {
            throw new NoObjectException("Это ваша собственная вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        checkBookingDates(LocalDate.now().atStartOfDay(), booking);
        booking.setItem(itemOpt.get());
        booking.setBooker(userOpt.get());
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Integer bookingId, Integer userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Integer ownerId = bookingOpt.get().getItem().getOwner().getId();
            Integer bookerId = bookingOpt.get().getBooker().getId();
            if (!ownerId.equals(userId) && !bookerId.equals(userId)) {
                throw new NoObjectException("Недостаточно прав на просмотр данного бронирования");
            }
            return BookingMapper.toBookingDto(bookingOpt.get());
        } else {
            throw new NoObjectException("Бронирование не найдено в системе");
        }
    }

    @Override
    public BookingDto setBookingStatus(Integer id, boolean approved, Integer userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) {
            throw new NoObjectException("Данное бронирование не найдено в системе");
        }
        Booking booking = bookingOpt.get();
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NoObjectException("Статус бронирования может изменять только собственник вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования уже изменен");
        }
        if (approved == true) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    @Override
    public List<BookingDto> getBookingsByUser(Integer userId, String state) {
        if (userId == null) {
            throw new NoObjectException("Не передан ID пользователя");
        } else if (userRepository.findById(userId).isEmpty()) {
            throw new NoObjectException("Указан некорректный пользователь");
        }
        BookingStateStatus enumState;
        try {
            enumState = Enum.valueOf(BookingStateStatus.class,state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        switch (enumState) {
            case ALL:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "start")));
            case WAITING:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start")));
            case REJECTED:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start")));
            case FUTURE:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case PAST:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case CURRENT:
                return BookingMapper.toBookingDtoList(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            default:
                return null;
        }
    }

    @Override
    public List<BookingDto> getBookingsByUsersItems(Integer userId, String state) {
        if (userId == null) {
            throw new NoObjectException("Не передан ID пользователя");
        } else if (userRepository.findById(userId).isEmpty()) {
            throw new NoObjectException("Указан некорректный пользователь");
        }
        List<Item> userItems = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<Integer> itemIds = new ArrayList<>();
        for (Item item : userItems) {
            itemIds.add(item.getId());
        }
        BookingStateStatus enumState;
        try {
            enumState = Enum.valueOf(BookingStateStatus.class,state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        switch (enumState) {
            case ALL:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdIn(itemIds, Sort.by(Sort.Direction.DESC, "start")));
            case WAITING:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start")));
            case REJECTED:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStatus(itemIds, BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start")));
            case FUTURE:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStartIsAfter(itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case PAST:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndEndIsBefore(itemIds, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            case CURRENT:
                return BookingMapper.toBookingDtoList(bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(itemIds, LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            default:
                throw new RuntimeException("Unknown state: " + state);
        }
    }

    private void checkBookingDates(LocalDateTime checkTime, Booking booking) {
        if (booking.getEnd() == null || booking.getStart() == null) {
            throw new ValidationException("Даты бронирования должны быть заполнены");
        }
        if (booking.getStart().isBefore(checkTime) || booking.getEnd().isBefore(checkTime)) {
            throw new ValidationException("Даты бронирования должны быть позже, чем время проверки");
        } else if (booking.getStart().equals(booking.getEnd())) {
            throw new ValidationException(("Бронирование должно длиться хотя бы 1 секунду"));
        } else if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Окончание бронирования должно быть позже старта бронирования");
        }
    }
}
