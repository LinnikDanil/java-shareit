package ru.practicum.shareit.request;

public class ItemRequestNotValidException extends RuntimeException {
    public ItemRequestNotValidException(String message) {
        super(message);
    }
}
