package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapperForItem {
    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) return null;
        return new BookingForItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}
