package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QCommentTest {
    @Test
    public void testQCommentConstructorWithVariable() {
        String variable = "testVariable";

        QComment qComment = new QComment(variable);

        assertNotNull(qComment);
        assertEquals(Comment.class, qComment.getType());
        assertEquals(variable, qComment.getMetadata().getName());
    }
}