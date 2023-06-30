package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    private User owner = User.builder()
            .name("owner")
            .email("owner@mail.ru")
            .build();

    private User booker = User.builder()
            .name("booker")
            .email("booker@mail.ru")
            .build();

    private Item item = Item.builder()
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(owner)
            .build();

    private Booking booking = Booking.builder()
            .start(LocalDateTime.of(2023, 6, 15, 10, 10))
            .end(LocalDateTime.of(2023, 6, 16, 10, 10))
            .item(item)
            .booker(booker)
            .status(BookingStatus.WAITING)
            .build();

    @AfterEach
    void clearRepository() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @BeforeEach
    void saveEntity() {
        userRepository.save(booker);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                booker.getId(),
                LocalDateTime.of(2023, 6, 15, 12, 10),
                LocalDateTime.of(2023, 6, 15, 12, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(
                booker.getId(), LocalDateTime.of(2023, 7, 28, 10, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(
                booker.getId(), LocalDateTime.of(2023, 5, 28, 10, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatus(
                booker.getId(), BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(
                owner.getId(),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                owner.getId(),
                LocalDateTime.of(2023, 6, 15, 12, 10),
                LocalDateTime.of(2023, 6, 15, 12, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                owner.getId(),
                LocalDateTime.of(2023, 6, 17, 10, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                owner.getId(),
                LocalDateTime.of(2023, 6, 14, 10, 10),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                owner.getId(),
                BookingStatus.WAITING,
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc() {
        Booking booking = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                item.getId(),
                owner.getId(),
                LocalDateTime.of(2023, 6, 16, 10, 10),
                BookingStatus.WAITING);

        assertEquals(booking.getId(), this.booking.getId());
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getBooker().getId(), booker.getId());
        assertEquals(booking.getStart(), this.booking.getStart());
        assertEquals(booking.getEnd(), this.booking.getEnd());
        assertEquals(booking.getStatus(), this.booking.getStatus());
    }

    @Test
    void findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc() {
        Booking booking = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                item.getId(),
                owner.getId(),
                LocalDateTime.of(2023, 6, 14, 10, 10),
                BookingStatus.WAITING);

        assertEquals(booking.getId(), this.booking.getId());
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getBooker().getId(), booker.getId());
        assertEquals(booking.getStart(), this.booking.getStart());
        assertEquals(booking.getEnd(), this.booking.getEnd());
        assertEquals(booking.getStatus(), this.booking.getStatus());
    }

    @Test
    void findFirstByItemIdAndBookerIdAndStatusAndEndBefore() {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                item.getId(),
                booker.getId(),
                BookingStatus.WAITING,
                LocalDateTime.of(2023, 6, 17, 10, 10)).get();

        assertEquals(booking.getId(), this.booking.getId());
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getBooker().getId(), booker.getId());
        assertEquals(booking.getStart(), this.booking.getStart());
        assertEquals(booking.getEnd(), this.booking.getEnd());
        assertEquals(booking.getStatus(), this.booking.getStatus());
    }

    @Test
    void findAll() {
        List<Booking> bookings = bookingRepository.findAll(Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }

    @Test
    void findLastBookings() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findLastBookings(
                owner.getId(),
                LocalDateTime.of(2023, 6, 17, 10, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());

        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void findNextBooking() {
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findNextBooking(
                owner.getId(),
                LocalDateTime.of(2023, 6, 14, 10, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());

        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(
                booker.getId(),
                Pageable.unpaged()).getContent();

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.get(0).getItem().getId(), item.getId());
        assertEquals(bookings.get(0).getBooker().getId(), booker.getId());
        assertEquals(bookings.get(0).getStart(), booking.getStart());
        assertEquals(bookings.get(0).getEnd(), booking.getEnd());
        assertEquals(bookings.get(0).getStatus(), booking.getStatus());
    }
}