package ru.practicum.shareit.item.repositrory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final UserRepository userRepository;
    private final HashMap<Long, Item> items;
    private long id = 0;

    @Override
    public List<Item> getUserItems(long userId) {
        userRepository.getUserById(userId); //Проверка на существование пользователя
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long itemId) {
        if (items.isEmpty()) {
            log.warn("Список предметов пуст.");
            throw new ItemNotFoundException("Список предметов пуст");
        }
        Item item = items.get(itemId);
        if (item == null) {
            log.warn("Предмета с id = {} не существует.", itemId);
            throw new ItemNotFoundException(String.format("Предмета с id = %s не существует", itemId));
        }

        return item;
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item addItem(Item item, long userId) {
        item.setOwner(userRepository.getUserById(userId));
        item.setId(generatedNewId());
        items.put(item.getId(), item);
        log.info("Предмет сохранён с id = {}.", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item, long itemId, long userId) {
        items.put(itemId, item);
        return item;
    }

    private long generatedNewId() {
        return ++id;
    }
}
