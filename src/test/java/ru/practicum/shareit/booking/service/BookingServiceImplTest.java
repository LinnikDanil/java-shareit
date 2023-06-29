package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.ItemUnavailableException;
import ru.practicum.shareit.booking.exception.UserNotOwnerBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private final User booker1 = User.builder()
            .id(1L)
            .name("booker1")
            .email("booker1@mail.ru")
            .build();
    private final User booker2 = User.builder()
            .id(2L)
            .name("booker2")
            .email("booker2@mail.ru")
            .build();
    private final User owner1 = User.builder()
            .id(2L)
            .name("owner")
            .email("owner@mail.ru")
            .build();
    private final User owner2 = User.builder()
            .id(3L)
            .name("owner2")
            .email("owner2@mail.ru")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(owner1)
            .build();
    private final BookingRequestDto bookingDto1 = new BookingRequestDto(
            item1.getId(),
            (LocalDateTime.now().plusHours(1)),
            (LocalDateTime.now().plusDays(1))
    );
    private final Item item2 = Item.builder()
            .id(2L)
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(false)
            .owner(owner2)
            .build();
    private final BookingRequestDto bookingDto2 = new BookingRequestDto(
            item2.getId(),
            (LocalDateTime.now().plusHours(1)),
            (LocalDateTime.now().plusDays(1))
    );
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        booking1 = BookingMapper.toBooking(bookingDto1, item1, booker1);
        booking1.setId(1L);
        booking2 = BookingMapper.toBooking(bookingDto2, item2, booker2);
        booking2.setId(2L);
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

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(Mockito.any(Booking.class));
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
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking1);

        BookingResponseDto bookingResponseDto = bookingService.confirmBooking(
                booking1.getId(),
                true,
                owner1.getId()
        );

        assertEquals(booking1.getId(), bookingResponseDto.getId());
        assertEquals(booking1.getStatus(), bookingResponseDto.getStatus());
        assertEquals(booking1.getStart(), bookingResponseDto.getStart());
        assertEquals(booking1.getEnd(), bookingResponseDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking1.getItem()), bookingResponseDto.getItem());
        assertEquals(UserMapper.toUserDto(booking1.getBooker()), bookingResponseDto.getBooker());

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void confirmBookingWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                BookingNotFoundException.class, () -> bookingService.confirmBooking(10L, true, owner1.getId()));

        String expectedMessage = "Бронирования с id = 10 не существует.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void confirmBookingAlreadyIsBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        booking1.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        Exception exception = assertThrows(
                BookingValidationException.class, () -> bookingService.confirmBooking(booking1.getId(), true, owner1.getId()));

        String expectedMessage = "Вещь уже забронирована.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void confirmBookingWithUserNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        Exception exception = assertThrows(
                UserNotOwnerBooking.class, () -> bookingService.confirmBooking(booking1.getId(), true, owner2.getId()));

        String expectedMessage = String.format("Пользователь с id = %s не является владельцем вещи, которую бронируют.", owner2.getId());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        BookingResponseDto bookingResponseDto = bookingService.getBooking(booking1.getId(), owner1.getId());

        assertEquals(booking1.getId(), bookingResponseDto.getId());
        assertEquals(booking1.getStatus(), bookingResponseDto.getStatus());
        assertEquals(booking1.getStart(), bookingResponseDto.getStart());
        assertEquals(booking1.getEnd(), bookingResponseDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking1.getItem()), bookingResponseDto.getItem());
        assertEquals(UserMapper.toUserDto(booking1.getBooker()), bookingResponseDto.getBooker());

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));

        when(bookingRepository.findById(10L))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(
                BookingNotFoundException.class, () -> bookingService.getBooking(10L, owner1.getId()));

        String expectedMessage = "Бронирования с id = 10 не существует.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    void getBookingWithUserNotOwnerOrBooker() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        Exception exception = assertThrows(
                UserNotOwnerBooking.class, () -> bookingService.getBooking(booking1.getId(), owner2.getId()));

        String expectedMessage = String.format("Пользователь с id = %s не имеет отношения к бронированию.", owner2.getId());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingsWithAllState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("ALL", booker1.getId(), 0, 10);

        assertEquals(2, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerId(anyLong(), any());
    }

    @Test
    void getBookingsWithCurrentState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("CURRENT", booker1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any());
    }

    @Test
    void getBookingsWithUnknownState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        String unknownState = "UNKNOWN";

        Exception exception = assertThrows(
                BookingValidationException.class,
                () -> bookingService.getBookings(unknownState, booker1.getId(), 0, 10));

        String expectedMessage = String.format("Unknown state: %s", unknownState);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        // Ни один из методов репозитория не должен вызываться
        verify(bookingRepository, never()).findAllByBookerId(anyLong(), any());
        verify(bookingRepository, never()).findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any(), any());
    }

    @Test
    void getBookingsWithPastState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(
                anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("PAST", booker1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdAndEndIsBefore(
                anyLong(), any(), any());
    }

    @Test
    void getBookingsWithFutureState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(
                anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("FUTURE", booker1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStartIsAfter(
                anyLong(), any(), any());
    }

    @Test
    void getBookingsWithWaitingState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(), eq(BookingStatus.WAITING), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("WAITING", booker1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(
                anyLong(), eq(BookingStatus.WAITING), any());
    }

    @Test
    void getBookingsWithRejectedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByBookerIdAndStatus(
                anyLong(), eq(BookingStatus.REJECTED), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getBookings("REJECTED", booker1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerIdAndStatus(
                anyLong(), eq(BookingStatus.REJECTED), any());
    }

    @Test
    void getOwnerBookingsWithAllState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("ALL", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerId(anyLong(), any());
    }

    @Test
    void getOwnerBookingsWithCurrentState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("CURRENT", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(), any(), any());
    }

    @Test
    void getOwnerBookingsWithPastState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("PAST", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(), any());
    }

    @Test
    void getOwnerBookingsWithFutureState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("FUTURE", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(), any());
    }

    @Test
    void getOwnerBookingsWithWaitingState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("WAITING", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void getOwnerBookingsWithRejectedState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<Booking> bookings = Collections.singletonList(booking1);
        Page<Booking> page = new PageImpl<>(bookings);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(), any())).thenReturn(page);

        List<BookingResponseDto> response = bookingService.getOwnerBookings("REJECTED", owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void getOwnerBookingsWithInvalidState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));

        Exception exception = assertThrows(BookingValidationException.class,
                () -> bookingService.getOwnerBookings("INVALID", owner1.getId(), 0, 10));

        String expectedMessage = "Unknown state: INVALID";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
    }
}