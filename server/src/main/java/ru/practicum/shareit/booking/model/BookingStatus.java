package ru.practicum.shareit.booking.model;

public enum BookingStatus {
    WAITING,  // Ожидает одобрения
    APPROVED, // Подтверждено владельцем
    REJECTED, // Отклонено владельцем
    CANCELED  // Отменено создателем
}
