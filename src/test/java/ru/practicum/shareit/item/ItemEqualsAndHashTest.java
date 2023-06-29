package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.*;

public class ItemEqualsAndHashTest {

    @Test
    public void testHashCode() {
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(1L);

        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testEquals() {
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(1L);

        assertTrue(item1.equals(item2) && item2.equals(item1));
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);

        assertFalse(item1.equals(item2) && item2.equals(item1));
    }

    @Test
    public void testEqualsWithNull() {
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = null;

        assertNotEquals(item1, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        Item item = new Item();
        item.setId(1L);

        assertNotEquals(item, "");
    }
}
