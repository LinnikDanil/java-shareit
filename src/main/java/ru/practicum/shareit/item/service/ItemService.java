package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getUserItems(long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(Item item, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);
}
