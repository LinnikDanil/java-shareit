package ru.practicum.shareit.booking.exception;

public class UserNotOwnerBooking extends RuntimeException {
    public UserNotOwnerBooking(String message) {
        super(message);
    }
}
