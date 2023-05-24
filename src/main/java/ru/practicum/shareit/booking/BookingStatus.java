package ru.practicum.shareit.booking;

public enum BookingStatus {
    WAITING,  // Ожидает одобрения
    APPROVED, // Подтверждено владельцем
    REJECTED, // Отклонено владельцем
    CANCELED  // Отменено создателем
}
