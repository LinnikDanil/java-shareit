package ru.practicum.shareit.check;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckerTest {
    @Test
    public void testCheckFromAndSizeNegativeFrom() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Checker.checkFromAndSize(-1, 10)
        );
        assertEquals("Валидация значений для пагинации не пройдена.", exception.getMessage());
    }

    @Test
    public void testCheckFromAndSizeZeroSize() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Checker.checkFromAndSize(1, 0)
        );
        assertEquals("Валидация значений для пагинации не пройдена.", exception.getMessage());
    }

    @Test
    public void testCheckFromAndSizeNegativeSize() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Checker.checkFromAndSize(1, -1)
        );
        assertEquals("Валидация значений для пагинации не пройдена.", exception.getMessage());
    }

    @Test
    public void testCheckFromAndSizeValidParameters() {
        assertDoesNotThrow(() -> Checker.checkFromAndSize(1, 10));
    }
}
