package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {
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

    @Query(value = "SELECT b1.* " +
            "FROM bookings b1 " +
            "JOIN (" +
                "SELECT item_id, MAX(start_date) as max_start_date " +
                "FROM BOOKINGS b2 " +
                "WHERE b2.ID in(" +
                    "SELECT b3.id FROM BOOKINGS b3 " +
                    "JOIN ITEMS i ON I.ID = B3.ITEM_ID " +
                    "WHERE i.OWNER_ID = ? " +
                    "AND b3.STATUS = 'APPROVED' " +
                    "AND b3.START_DATE < ?) " +
                "GROUP BY ITEM_ID) b2 " +
            "ON b1.item_id = b2.item_id AND b1.start_date = b2.max_start_date", nativeQuery = true)
    List<Booking> findLastBookings(
            Long ownerId, LocalDateTime startDate);

    @Query(value = "SELECT b1.* " +
            "FROM bookings b1 " +
            "JOIN (" +
                "SELECT item_id, MIN(start_date) as min_start_date " +
                "FROM BOOKINGS b2 " +
                "WHERE b2.ID in(" +
                    "SELECT b3.id FROM BOOKINGS b3 " +
                    "JOIN ITEMS i ON I.ID = B3.ITEM_ID " +
                    "WHERE i.OWNER_ID = ? " +
                    "AND b3.STATUS = 'APPROVED' " +
                    "AND b3.START_DATE >= ?) " +
                "GROUP BY ITEM_ID) b2 " +
            "ON b1.item_id = b2.item_id AND b1.start_date = b2.min_start_date", nativeQuery = true)
    List<Booking> findNextBooking(
            Long ownerId, LocalDateTime startDate);
}
