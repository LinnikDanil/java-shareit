package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestEqualsAndHashTest {

    @Test
    public void testHashCode() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(1L);

        assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }

    @Test
    public void testEquals() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(1L);

        assertTrue(itemRequest1.equals(itemRequest2) && itemRequest2.equals(itemRequest1));
        assertEquals(itemRequest1.hashCode(), itemRequest2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);

        assertFalse(itemRequest1.equals(itemRequest2) && itemRequest2.equals(itemRequest1));
    }

    @Test
    public void testEqualsWithNull() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);

        assertNotEquals(itemRequest1, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        assertNotEquals(itemRequest, "");
    }
}
