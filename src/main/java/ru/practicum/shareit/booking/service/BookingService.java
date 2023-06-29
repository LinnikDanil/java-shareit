package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto confirmBooking(Long bookingId, boolean approved, long userId);

    BookingResponseDto getBooking(Long bookingId, long userId);

    List<BookingResponseDto> getBookings(String state, long userId, int from, int size);

    List<BookingResponseDto> getOwnerBookings(String state, long userId, int from, int size);
}
