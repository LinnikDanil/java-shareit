package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {
    @Test
    void getError() {
        ErrorResponse errorResponse = new ErrorResponse("message");

        assertEquals(errorResponse.getError(), "message");
    }
}