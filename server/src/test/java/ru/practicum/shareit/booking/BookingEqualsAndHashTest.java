package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.*;

public class BookingEqualsAndHashTest {

    @Test
    public void testHashCode() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    public void testEquals() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(1L);

        assertTrue(booking1.equals(booking2) && booking2.equals(booking1));
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        Booking booking1 = new Booking();
        booking1.setId(1L);
        Booking booking2 = new Booking();
        booking2.setId(2L);

        assertFalse(booking1.equals(booking2) && booking2.equals(booking1));
    }

    @Test
    public void testEqualsWithNull() {
        Booking booking1 = new Booking();
        booking1.setId(1L);

        assertNotEquals(booking1, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        Booking booking = new Booking();
        booking.setId(1L);

        assertNotEquals(booking, "");
    }
}
