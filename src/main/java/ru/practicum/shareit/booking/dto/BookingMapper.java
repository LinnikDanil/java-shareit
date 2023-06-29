package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingRequestDto bookingDto, Item item, User user) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        //Проверка времени
        if (start == null || end == null) {
            throw new BookingValidationException("Время не может быть null");
        }
        if (start.isAfter(end) || start.isEqual(end) || start.isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Некорректное время");
        }

        return new Booking(
                null,
                start,
                end,
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public List<BookingResponseDto> toBookingResponseDto(Iterable<Booking> bookings) {
        List<BookingResponseDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking != null) {
                bookingsDto.add(toBookingResponseDto(booking));
            }
        }
        return bookingsDto;
    }

    public BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) return null;
        return new BookingForItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
