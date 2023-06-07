package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto confirmBooking(Long bookingId, boolean approved, long userId);

    BookingResponseDto getBooking(Long bookingId, long userId);

    List<BookingResponseDto> getBookings(String state, long userId);

    List<BookingResponseDto> getOwnerBookings(String state, long userId);
}
