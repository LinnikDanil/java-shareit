package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommentEqualsAndHashTest {
    @Test
    public void testHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    public void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertTrue(comment1.equals(comment2) && comment2.equals(comment1));
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentIds() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        Comment comment2 = new Comment();
        comment2.setId(2L);

        assertFalse(comment1.equals(comment2) && comment2.equals(comment1));
    }

    @Test
    public void testEqualsWithNull() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        assertNotEquals(comment1, null);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        Comment comment = new Comment();
        comment.setId(1L);

        assertNotEquals(comment, "");
    }
}