package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long bookerId) {
        // Проверка на существования пользователя
        checkUser(bookerId);

        //Проверка на существование предмета
        Item item = itemRepository.findById(bookingRequestDto.getItemId()).orElseThrow(() -> new UserNotFoundException(
                String.format("Предмета с id = %s не существует", bookingRequestDto.getItemId())));

        //Проверка на доступность предмета
        if (!item.getAvailable()) {
            throw new ItemUnavailableException("Статус данной вещи недоступен.");
        }

        Booking booking = bookingMapper.toBooking(bookingRequestDto, bookerId);

        //Проверка пользователь - владелец вещи
        if (item.getOwner().getId().equals(bookerId)) {
            throw new BookingNotFoundException("Пользователь не может арендовать свою же вещь.");
        }

        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
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
                    String.format("Пользователь с id = %s не является владельцем бронирования.", userId));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
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

        return bookingMapper.toBookingResponseDto(booking);
    }

    /**
     * Выводит список всех броней у пользователя, который их брал
     **/
    @Transactional
    @Override
    public List<BookingResponseDto> getBookings(String state, long userId) {
        checkUser(userId);
        List<Booking> bookings;
        Sort sortByBookingStartDesc = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAll(sortByBookingStartDesc);
                break;
            case "CURRENT": //текущие - между start и end
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "PAST": //завершённые - позже end
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "FUTURE": //будущие - раньше start
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "WAITING": //Ожидают - waiting
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId, BookingStatus.WAITING, sortByBookingStartDesc);
                break;
            case "REJECTED": //отклонённые - rejected
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId, BookingStatus.REJECTED, sortByBookingStartDesc);
                break;
            default:
                throw new BookingValidationException(String.format("Unknown state: %s", state));
        }

        return bookingMapper.toBookingResponseDto(bookings);
    }

    /**
     * Выводит список всех бронирований для вещей у пользователя, который их выставлял
     **/
    @Transactional
    @Override
    public List<BookingResponseDto> getOwnerBookings(String state, long userId) {
        checkUser(userId);
        List<Booking> bookings;
        Sort sortByBookingStartDesc = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(userId, sortByBookingStartDesc);
                break;
            case "CURRENT": //текущие - между start и end
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "PAST": //завершённые - позже end
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "FUTURE": //будущие - раньше start
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), sortByBookingStartDesc);
                break;
            case "WAITING": //Ожидают - waiting
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId, BookingStatus.WAITING, sortByBookingStartDesc);
                break;
            case "REJECTED": //отклонённые - rejected
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED, sortByBookingStartDesc);
                break;
            default:
                throw new BookingValidationException(String.format("Unknown state: %s", state));
        }

        return bookingMapper.toBookingResponseDto(bookings);
    }

    private void checkUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователя с id = %s не существует", userId)));
    }
}
