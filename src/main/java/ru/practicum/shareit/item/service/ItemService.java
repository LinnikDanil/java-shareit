package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.util.List;

public interface ItemService {
    List<ItemFullDto> getUserItems(long userId);

    ItemFullDto getItemById(long itemId, long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    CommentDto addComment(CommentDto commentDto, long itemId, long userId);
}
