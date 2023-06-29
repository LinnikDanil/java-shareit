package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        // Проверка на существования пользователя
        User user = checkUser(bookerId);

        //Проверка на существование предмета
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> new ItemNotFoundException(
                String.format("Предмета с id = %s не существует", bookingRequestDto.getItemId())));

        //Проверка на доступность предмета
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Статус данной вещи недоступен.");
        }

        //Проверка пользователь - владелец вещи
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingNotFoundException("Пользователь не может арендовать свою же вещь.");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, item, user);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto confirmBooking(Long bookingId, boolean approved, long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Бронирования с id = %s не существует.", bookingId)));

        if (booking.getItem().getOwner().getId().equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingValidationException("Вещь уже забронирована.");
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotOwnerBooking(
                    String.format("Пользователь с id = %s не является владельцем вещи, которую бронируют.", userId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto getBooking(Long bookingId, long userId) {
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(
                String.format("Бронирования с id = %s не существует.", bookingId)));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserNotOwnerBooking(
                    String.format("Пользователь с id = %s не имеет отношения к бронированию.", userId));
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    /**
     * Выводит список всех броней у пользователя, который их брал
     **/
    @Transactional
    @Override
    public List<BookingResponseDto> getBookings(String state, long userId, int from, int size) {
        checkUser(userId);
        Page<Booking> bookingsPage;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());

        switch (state) {
            case "ALL":
                bookingsPage = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case "CURRENT": //текущие - между start и end
                bookingsPage = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST": //завершённые - позже end
                bookingsPage = bookingRepository.findAllByBookerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE": //будущие - раньше start
                bookingsPage = bookingRepository.findAllByBookerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING": //Ожидают - waiting
                bookingsPage = bookingRepository.findAllByBookerIdAndStatus(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED": //отклонённые - rejected
                bookingsPage = bookingRepository.findAllByBookerIdAndStatus(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new BookingValidationException(String.format("Unknown state: %s", state));
        }

        List<Booking> bookings = bookingsPage.getContent();
        return BookingMapper.toBookingResponseDto(bookings);
    }

    /**
     * Выводит список всех бронирований для вещей у пользователя, который их выставлял
     **/
    @Transactional
    @Override
    public List<BookingResponseDto> getOwnerBookings(String state, long userId, int from, int size) {
        checkUser(userId);
        Page<Booking> bookingsPage;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("start").descending());

        switch (state) {
            case "ALL":
                bookingsPage = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case "CURRENT": //текущие - между start и end
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST": //завершённые - позже end
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE": //будущие - раньше start
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING": //Ожидают - waiting
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED": //отклонённые - rejected
                bookingsPage = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new BookingValidationException(String.format("Unknown state: %s", state));
        }

        List<Booking> bookings = bookingsPage.getContent();
        return BookingMapper.toBookingResponseDto(bookings);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с id = %s не существует", userId)));
    }
}
