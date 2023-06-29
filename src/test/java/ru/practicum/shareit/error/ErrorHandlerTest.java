package ru.practicum.shareit.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.exception.ItemUnavailableException;
import ru.practicum.shareit.booking.exception.UserNotOwnerBooking;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIsDefferentException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
class ErrorHandlerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserController userController;
    @MockBean
    private ItemController itemController;
    @MockBean
    private BookingController bookingController;
    @MockBean
    private ItemRequestController itemRequestController;

    //user
    @Test
    void handlerUserNotFoundException() throws Exception {
        when(userController.getAllUsers())
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found")));
    }

    @Test
    void handlerUserAlreadyExistException() throws Exception {
        when(userController.updateUser(any(UserDto.class), anyLong()))
                .thenThrow(new UserAlreadyExistException("User already exist"));


        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("User already exist")));
    }

    //item
    @Test
    void handlerItemNotFoundException() throws Exception {
        when(itemController.getItemById(anyLong(), anyLong()))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item not found")));
    }

    @Test
    void handlerItemOwnerIsDifferentException() throws Exception {
        when(itemController.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenThrow(new ItemOwnerIsDefferentException("Item owner is different"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Item owner is different")));
    }

    //booking
    @Test
    void handlerItemUnavailableException() throws Exception {
        when(bookingController.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenThrow(new ItemUnavailableException("Item is unavailable"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequestDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Item is unavailable")));
    }

    @Test
    void handlerBookingNotFoundException() throws Exception {
        when(bookingController.createBooking(any(BookingRequestDto.class), anyLong()))
                .thenThrow(new BookingNotFoundException("Booking Not Found"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BookingRequestDto())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Booking Not Found")));
    }

    @Test
    void handlerUserNotOwnerBooking() throws Exception {
        when(bookingController.getBooking(anyLong(), anyLong()))
                .thenThrow(new UserNotOwnerBooking("User Not Owner"));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User Not Owner")));
    }

    @Test
    void handlerBookingValidationException() throws Exception {
        when(bookingController.getBookings(anyString(), anyLong(), anyInt(), anyInt()))
                .thenThrow(new BookingValidationException("validation exception"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("validation exception")));
    }

    //itemRequest
    @Test
    void handlerItemRequestNotFoundException() throws Exception {
        when(itemRequestController.getRequestById(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotFoundException("Item Request Not Found"));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Item Request Not Found")));
    }

    @Test
    void handlerIllegalArgumentException() throws Exception {
        when(userController.getAllUsers())
                .thenThrow(new IllegalArgumentException("Invalid argument"));

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Invalid argument")));
    }

    @Test
    void handlerThrowable() throws Exception {
        when(userController.getAllUsers())
                .thenThrow(new IllegalStateException("Something went wrong"));

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Something went wrong")));
    }


}