package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(
            1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1)
    );
    private final BookingResponseDto bookingResponseDto = new BookingResponseDto(
            1L, bookingRequestDto.getStart(), bookingRequestDto.getEnd(), new ItemDto(), new UserDto(), BookingStatus.WAITING
    );
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Создание бронирования")
    void createBooking() throws Exception {
        when(bookingService.createBooking(Mockito.any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void confirmBooking() throws Exception {
        BookingResponseDto approvedBookingResponseDto = new BookingResponseDto(
                1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), new ItemDto(), new UserDto(), BookingStatus.APPROVED
        );
        when(bookingService.confirmBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(approvedBookingResponseDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    @DisplayName("Получение бронирования")
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class));
    }

    @Test
    @DisplayName("Получение забронированных предметов")
    void getBookings() throws Exception {
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);
        when(bookingService.getBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class));
    }

    @DisplayName("Получение списка броинрования для предметов пользователя")
    @Test
    void getOwnerBookings() throws Exception {
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);
        when(bookingService.getOwnerBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1), Integer.class));
    }
}