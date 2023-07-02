package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(BookingRequestDto bookingDto, Item item, User user) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
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
