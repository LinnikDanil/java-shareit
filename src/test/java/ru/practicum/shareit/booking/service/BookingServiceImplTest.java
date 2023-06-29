package ru.practicum.shareit.booking.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.ItemUnavailableException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    private final EasyRandom easyRandom = new EasyRandom();

    private User booker1 = User.builder()
            .id(1L)
            .name("booker1")
            .email("booker1@mail.ru")
            .build();

    private User booker2 = User.builder()
            .id(2L)
            .name("booker2")
            .email("booker2@mail.ru")
            .build();

    private User owner1 = User.builder()
            .id(2L)
            .name("owner")
            .email("owner@mail.ru")
            .build();

    private User owner2 = User.builder()
            .id(3L)
            .name("owner2")
            .email("owner2@mail.ru")
            .build();

    private Item item1 = Item.builder()
            .id(1L)
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(owner1)
            .build();

    private Item item2 = Item.builder()
            .id(2L)
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(false)
            .owner(owner2)
            .build();

    private BookingRequestDto bookingDto1 = new BookingRequestDto(
            item1.getId(),
            (LocalDateTime.now().plusHours(1)),
            (LocalDateTime.now().plusDays(1))
    );

    private BookingRequestDto bookingDto2 = new BookingRequestDto(
            item2.getId(),
            (LocalDateTime.now().plusHours(1)),
            (LocalDateTime.now().plusDays(1))
    );

    private Booking booking1;
    private Booking booking2;


    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        booking1 = BookingMapper.toBooking(bookingDto1, item1, booker1);
        booking1.setId(1L);
        booking2 = BookingMapper.toBooking(bookingDto2, item2, booker2);
        booking1.setId(2L);
    }

    @Test
    void createBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingResponseDto bookingResponseDto = bookingService.createBooking(
                bookingDto1,
                booker1.getId()
        );

        assertEquals(booking1.getId(), bookingResponseDto.getId());
        assertEquals(booking1.getStatus(), bookingResponseDto.getStatus());
        assertEquals(booking1.getStart(), bookingResponseDto.getStart());
        assertEquals(booking1.getEnd(), bookingResponseDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking1.getItem()), bookingResponseDto.getItem());
        assertEquals(UserMapper.toUserDto(booking1.getBooker()), bookingResponseDto.getBooker());
    }

    @Test
    void createBookingWithWrongBookerId() {
        when(userRepository.findById(10L))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class, () -> bookingService.createBooking(bookingDto1, 10L));

        String expectedMessage = "Пользователя с id = 10 не существует";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWithoutItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));

        when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ItemNotFoundException.class, () -> bookingService.createBooking(bookingDto1, booker1.getId()));

        String expectedMessage = "Предмета с id = " + booker1.getId() + " не существует";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }


    @Test
    void createBookingWithNotAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));

        assertThrows(ItemUnavailableException.class, () -> bookingService.createBooking(bookingDto2, booker2.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void createBookingWithUserNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(BookingNotFoundException.class, () -> bookingService.createBooking(bookingDto1, booker2.getId()));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void confirmBooking() {
    }

    @Test
    void getBooking() {
    }

    @Test
    void getBookings() {
    }

    @Test
    void getOwnerBookings() {
    }
}