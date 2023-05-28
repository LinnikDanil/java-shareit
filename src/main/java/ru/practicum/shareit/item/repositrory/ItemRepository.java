package ru.practicum.shareit.item.repositrory;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getUserItems(long userId);

    Item getItemById(long itemId);

    List<Item> searchItems(String text);

    Item addItem(Item item, long userId);

    Item updateItem(Item item, long itemId, long userId);
}
