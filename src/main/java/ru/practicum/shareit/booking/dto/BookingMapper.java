package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserService userService;
    private final ItemService itemService;

    public Booking toBooking(BookingRequestDto bookingDto, Long userId) {
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
                ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId(), userId)),
                UserMapper.toUser(userService.getUserById(userId)),
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
            bookingsDto.add(toBookingResponseDto(booking));
        }
        return bookingsDto;
    }
}
