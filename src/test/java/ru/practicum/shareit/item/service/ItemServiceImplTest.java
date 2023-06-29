package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIsDefferentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.CommentRepository;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
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
    private ItemService itemService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, bookingRepository);
    }

    @Test
    void getUserItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        Page<Item> page = new PageImpl<>(Collections.singletonList(item1));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(page);
        when(commentRepository.findAllByItemIdIn(any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookings(anyLong(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findNextBooking(anyLong(), any())).thenReturn(Collections.emptyList());

        List<ItemFullDto> response = itemService.getUserItems(owner1.getId(), 0, 10);

        assertEquals(1, response.size());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(commentRepository, times(1)).findAllByItemIdIn(any());
        verify(bookingRepository, times(1)).findLastBookings(anyLong(), any());
        verify(bookingRepository, times(1)).findNextBooking(anyLong(), any());
    }

    @Test
    void getUserItemsWithNonExistingUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.getUserItems(10L, 0, 10));

        String expectedMessage = "Пользователь с id = 10 не найден";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findAllByOwnerId(anyLong(), any());
        verify(commentRepository, never()).findAllByItemIdIn(any());
        verify(bookingRepository, never()).findLastBookings(anyLong(), any());
        verify(bookingRepository, never()).findNextBooking(anyLong(), any());
    }

    @Test
    void getItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(anyLong(), anyLong(), any(), any()))
                .thenReturn(Booking.builder().id(owner2.getId()).booker(owner2).build());
        when(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), anyLong(), any(), any()))
                .thenReturn(null);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemFullDto response = itemService.getItemById(item1.getId(), owner1.getId());
        assertEquals(item1.getId(), response.getId());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                anyLong(), anyLong(), any(), any());
        verify(bookingRepository, times(1)).findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void getItemByIdWithNonExistingItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(10L, owner1.getId()));

        String expectedMessage = String.format("Предмета с id = %s не существует", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                anyLong(), anyLong(), any(), any());
        verify(bookingRepository, never()).findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, never()).findAllByItemId(anyLong());
    }

    @Test
    void searchItems() {
        String searchText = "бензопила";
        Page<Item> page = new PageImpl<>(Collections.singletonList(item1));
        when(itemRepository.search(anyString(), any())).thenReturn(page);

        List<ItemDto> response = itemService.searchItems(searchText, 0, 10);

        assertEquals(1, response.size());
        verify(itemRepository, times(1)).search(anyString(), any());
    }

    @Test
    void searchItemsWithEmptyText() {
        String searchText = "";

        List<ItemDto> response = itemService.searchItems(searchText, 0, 10);

        assertEquals(0, response.size());
        verify(itemRepository, never()).search(anyString(), any());
    }

    @Test
    void addItemW() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));

        ItemDto itemDto = ItemMapper.toItemDto(item1);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner1);

        when(itemRepository.save(any())).thenReturn(item);

        ItemDto response = itemService.addItem(itemDto, owner1.getId());
        assertEquals(itemDto.getId(), response.getId());
        assertEquals(itemDto.getName(), response.getName());
        assertEquals(itemDto.getDescription(), response.getDescription());
        assertEquals(itemDto.getAvailable(), response.getAvailable());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addItemWithNonExistingUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemDto itemDto = ItemMapper.toItemDto(item1);

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.addItem(itemDto, 10L));

        String expectedMessage = String.format("Пользователь с id = %s не найден", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        ItemDto newItemDto = ItemDto.builder()
                .name("Новая бензопила")
                .description("Новое описание бензопилы")
                .available(false)
                .build();

        when(itemRepository.save(any())).thenReturn(item1);

        ItemDto updatedItem = itemService.updateItem(newItemDto, item1.getId(), owner1.getId());
        assertEquals(newItemDto.getName(), updatedItem.getName());
        assertEquals(newItemDto.getDescription(), updatedItem.getDescription());
        assertEquals(newItemDto.getAvailable(), updatedItem.getAvailable());

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItemWithNonExistingItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemDto newItemDto = ItemDto.builder()
                .name("Новая бензопила")
                .description("Новое описание бензопилы")
                .available(false)
                .build();

        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.updateItem(newItemDto, 10L, owner1.getId()));

        String expectedMessage = String.format("Предмета с id = %s не существует", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemWithWrongOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        ItemDto newItemDto = ItemDto.builder()
                .name("Новая бензопила")
                .description("Новое описание бензопилы")
                .available(false)
                .build();

        Exception exception = assertThrows(
                ItemOwnerIsDefferentException.class,
                () -> itemService.updateItem(newItemDto, item1.getId(), owner2.getId()));

        String expectedMessage = String.format("Невозможно обновить предмет с id = %s, " +
                        "так как у его владельца id = %d, а в аргумент поступило id = %d.",
                item1.getId(), owner1.getId(), owner2.getId());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addCommentWithValidData() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        Booking booking = Booking.builder()
                .id(1L)
                .item(item1)
                .booker(owner1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(Optional.of(booking));

        Comment comment = Comment.builder()
                .id(1L)
                .text("Отличный товар!")
                .item(item1)
                .author(owner1)
                .created(LocalDateTime.now())
                .build();
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        CommentDto response = itemService.addComment(commentDto, item1.getId(), owner1.getId());

        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getText(), response.getText());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void addCommentWithoutUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CommentDto commentDto = new CommentDto();

        Exception exception = assertThrows(
                UserNotFoundException.class,
                () -> itemService.addComment(commentDto, item1.getId(), 10L));

        String expectedMessage = String.format("Пользователь с id = %s не найден", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentWithNonExistingItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        CommentDto commentDto = new CommentDto();

        Exception exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.addComment(commentDto, 10L, owner1.getId()));

        String expectedMessage = String.format("Предмета с id = %s не существует", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addCommentWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any())).thenReturn(Optional.empty());

        CommentDto commentDto = new CommentDto();

        Exception exception = assertThrows(
                BookingValidationException.class,
                () -> itemService.addComment(commentDto, item1.getId(), owner1.getId()));

        String expectedMessage = "Пользователь не бронировал данную вещь";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(), any());
        verify(commentRepository, never()).save(any());
    }
}