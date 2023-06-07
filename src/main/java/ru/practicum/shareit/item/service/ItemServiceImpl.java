package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public List<ItemFullDto> getUserItems(long userId) {
        log.info("Вывод всех предметов пользователя с id = {}:", userId);

        //Проверка на существование пользователя
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));

        List<ItemFullDto> itemsDto = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerId(userId);



        for (Item item : items) {
            BookingForItemDto lastBooking = BookingMapperForItem.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                            item.getId(), userId, LocalDateTime.now(), BookingStatus.APPROVED));
            BookingForItemDto nextBooking = BookingMapperForItem.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                            item.getId(), userId, LocalDateTime.now(), BookingStatus.APPROVED));
            List<CommentDto> commentsDto = CommentMapper.toCommentDto(commentRepository.findAllByItemId(item.getId()));
            itemsDto.add(ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, commentsDto));
        }
        itemsDto.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));

        return itemsDto;
    }

    @Transactional
    @Override
    public ItemFullDto getItemById(long itemId, long userId) {
        log.info("Вывод предмета с id = {}:", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Предмета с id = %s не существует", itemId)));

        Booking lastBook = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(
                item.getId(), userId, LocalDateTime.now(), BookingStatus.APPROVED);
        BookingForItemDto lastBooking = BookingMapperForItem.toBookingForItemDto(lastBook);

        Booking nextBook = null;
        if (lastBook != null) {
            nextBook = bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
                    item.getId(), userId, lastBook.getStart(), BookingStatus.APPROVED);
        }
        BookingForItemDto nextBooking = BookingMapperForItem.toBookingForItemDto(nextBook);

        List<CommentDto> commentsDto = CommentMapper.toCommentDto(commentRepository.findAllByItemId(itemId));

        return ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, commentsDto);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск предметов содержащих <{}>:", text);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        log.info("Добавление предмета пользователю с id = {}:", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Обновление предмета с id = {} у пользователя с id = {}:", itemId, userId);

        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Предмета с id = %s не существует", itemId)));

        if (existingItem.getOwner().getId() != userId) {
            throw new ItemOwnerIsDefferentException(String.format("Невозможно обновить предмет с id = %s, " +
                            "так как у его владельца id = %d, а в аргумент поступило id = %d.",
                    itemId, existingItem.getOwner().getId(), userId));
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (name != null) {
            existingItem.setName(name);
            log.info("Имя предмета обновлено на {}.", name);
        }

        if (description != null) {
            existingItem.setDescription(description);
            log.info("Описание предмета обновлено на {}.", description);
        }

        if (available != null && available != existingItem.getAvailable()) {
            existingItem.setAvailable(available);
            log.info("Статус предмета обновлён на {}.", available);
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Предмета с id = %s не существует", itemId)));

        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new BookingValidationException("Пользователь не бронировал данную вещь"));

        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDto(comment);
    }
}
