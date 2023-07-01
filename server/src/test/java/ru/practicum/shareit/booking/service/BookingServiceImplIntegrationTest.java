package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final BookingServiceImpl bookingService;

    private final User user = User.builder().name("user").email("user@mail.ru").build();
    private final Item item = Item.builder().name("itemName").description("item1Desc").available(true).owner(user).build();
    private final Booking booking = Booking.builder().booker(user).item(item).status(BookingStatus.WAITING).start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1)).build();
    private final Booking secondBooking = Booking.builder().booker(user).item(item).status(BookingStatus.REJECTED).start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(4)).build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
        bookingRepository.save(secondBooking);
    }

    @Test
    void getBookings() {
        Collection<BookingResponseDto> userBookings = bookingService.getBookings("ALL", user.getId(), 0, 10);

        assertNotNull(userBookings);
        assertEquals(2, userBookings.size());

        BookingResponseDto firstBooking = userBookings.stream().findFirst().orElse(null);
        assertNotNull(firstBooking);
        assertNotNull(firstBooking.getBooker());
        assertNotNull(firstBooking.getStatus());
        assertNotNull(firstBooking.getStart());
        assertNotNull(firstBooking.getEnd());

        BookingResponseDto secondBooking = userBookings.stream().skip(1).findFirst().orElse(null);
        assertNotNull(secondBooking);
        assertNotNull(secondBooking.getBooker());
        assertNotNull(secondBooking.getStatus());
        assertNotNull(secondBooking.getStart());
        assertNotNull(secondBooking.getEnd());
    }
}

