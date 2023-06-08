package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwnerId(long userId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus status, Sort sort);

    Booking findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
            long itemId, long ownerId, LocalDateTime time, BookingStatus status);

    Booking findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
            long itemId, long ownerId, LocalDateTime time, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
            long itemId, long bookerId, BookingStatus status, LocalDateTime end);
}
