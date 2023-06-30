package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    private final User owner1 = User.builder()
            .id(1L)
            .name("owner")
            .email("owner@mail.ru")
            .build();
    private final User owner2 = User.builder()
            .id(2L)
            .name("owner2")
            .email("owner2@mail.ru")
            .build();
    private final Item item1 = Item.builder()
            .id(1L)
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(owner1)
            .build();
    private final Item item2 = Item.builder()
            .id(2L)
            .name("Газонокосилка")
            .description("Аккумуляторная газонокосилка")
            .available(false)
            .owner(owner2)
            .build();
    private final ItemRequest itemRequest1 = ItemRequest.builder()
            .id(1L)
            .user(owner2)
            .description("Нужна бензопила")
            .created(LocalDateTime.now())
            .build();
    private final ItemRequest itemRequest2 = ItemRequest.builder()
            .id(2L)
            .user(owner1)
            .description("Нужна газонокосилка")
            .created(LocalDateTime.now())
            .build();
    private final ItemRequestDto itemRequestDto = new ItemRequestDto("Нужна газонокосилка");
    private ItemRequestService itemRequestService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createRequest() {
        ItemRequest expectedItemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, owner1);

        when(userRepository.findById(owner1.getId())).thenReturn(Optional.of(owner1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(expectedItemRequest);

        ItemRequestResponseDto response = itemRequestService.createRequest(itemRequestDto, owner1.getId());

        assertEquals(expectedItemRequest.getId(), response.getId());
        assertEquals(expectedItemRequest.getUser().getId(), response.getUser().getId());
        assertEquals(expectedItemRequest.getDescription(), response.getDescription());
        assertEquals(expectedItemRequest.getCreated(), response.getCreated());

        verify(userRepository, times(1)).findById(owner1.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequestWithNonExistingUser() {
        when(userRepository.findById(owner1.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.createRequest(itemRequestDto, owner1.getId())
        );

        String expectedMessage = String.format("Пользователь с id = %s не найден", owner1.getId());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(owner1.getId());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getOwnerRequests() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        List<ItemRequest> itemRequests = Collections.singletonList(itemRequest2);
        when(itemRequestRepository.findAllByUserIdOrderByCreatedDesc(anyLong())).thenReturn(itemRequests);

        List<ItemRequestResponseFullDto> response = itemRequestService.getOwnerRequests(owner1.getId());

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findAllByUserIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getOwnerRequestsWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getOwnerRequests(10L));

        String expectedMessage = "Пользователь с id = 10 не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findAllByUserIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getUserRequests() {
        Page<ItemRequest> itemRequestPage = new PageImpl<>(Arrays.asList(itemRequest1, itemRequest2));
        when(itemRequestRepository.findAllByUserIdNot(anyLong(), any(Pageable.class))).thenReturn(itemRequestPage);

        List<ItemRequestResponseFullDto> response = itemRequestService.getUserRequests(owner1.getId(), 0, 2);

        assertEquals(2, response.size());
        verify(itemRequestRepository, times(1)).findAllByUserIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequest(anyLong())).thenReturn(Arrays.asList(item1, item2));

        ItemRequestResponseFullDto response = itemRequestService.getRequestById(itemRequest1.getId(), owner1.getId());

        assertEquals(itemRequest1.getId(), response.getId());
        assertEquals(2, response.getItems().size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByRequest(anyLong());
    }

    @Test
    void getRequestByIdWithUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemRequestService.getRequestById(itemRequest1.getId(), 10L));

        String expectedMessage = "Пользователь с id = 10 не имеет права искать запросы";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findAllByRequest(anyLong());
    }

    @Test
    void getRequestByIdWithRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(10L, owner1.getId()));

        String expectedMessage = "Запрос на предмет с id = 10 не найден.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findAllByRequest(anyLong());
    }
}