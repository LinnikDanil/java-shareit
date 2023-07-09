package ru.practicum.shareit.item.exception;

public class ItemOwnerIsDefferentException extends RuntimeException {
    public ItemOwnerIsDefferentException(String message) {
        super(message);
    }
}
