package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEqualsAndHashTest {

    @Test
    public void testHashCode() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(1L);

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void testEquals() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(1L);

        assertTrue(user1.equals(user2) && user2.equals(user1));
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        assertFalse(user1.equals(user2) && user2.equals(user1));
    }

    @Test
    public void testEqualsWithNull() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = null;

        assertNotEquals(user1, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        User user = new User();
        user.setId(1L);

        assertNotEquals(user, "");
    }
}
