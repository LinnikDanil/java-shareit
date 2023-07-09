package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final User user = new User(1L, "username", "email@mail.ru");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("Need an item");
    private final ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(1L, user, "Need an item", LocalDateTime.now());
    private final ItemDto itemDto = new ItemDto(1L, "Item1", "This is item 1", true, null);
    private final ItemRequestResponseFullDto itemRequestResponseFullDto = new ItemRequestResponseFullDto(
            1L, "Need an item", LocalDateTime.now(), Collections.singletonList(itemDto));
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mvc;

    @Test
    void createRequest() throws Exception {
        when(itemRequestService.createRequest(Mockito.any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestResponseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Need an item")));
    }

    @Test
    void getOwnerRequests() throws Exception {
        List<ItemRequestResponseFullDto> itemRequests = List.of(itemRequestResponseFullDto);
        when(itemRequestService.getOwnerRequests(anyLong()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need an item")));
    }

    @Test
    void getUserRequests() throws Exception {
        List<ItemRequestResponseFullDto> itemRequests = List.of(itemRequestResponseFullDto);
        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequests);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Need an item")));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestResponseFullDto);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Need an item")));
    }
}
