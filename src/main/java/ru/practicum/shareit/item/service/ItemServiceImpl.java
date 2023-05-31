package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemOwnerIsDefferentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getUserItems(long userId) {
        log.info("Вывод всех предметов пользователя с id = {}:", userId);

        userRepository.getUserById(userId); //Проверка на существование пользователя

        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Вывод предмета с id = {}:", itemId);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Поиск предметов содержащих <{}>:", text);
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        log.info("Добавление предмета пользователю с id = {}:", userId);

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));

        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Обновление предмета с id = {} у пользователя с id = {}:", itemId, userId);
        Item existingItem = itemRepository.getItemById(itemId);

        if (existingItem.getOwner().getId() != userId) {
            throw new ItemOwnerIsDefferentException(String.format("Невозможно обновить предмет с id = %s, " +
                            "так как у его владельца id = %d, а в аргумент поступило id = %d.",
                    itemId, existingItem.getOwner().getId(), userId));
        }

        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();

        if (name != null) {
            log.info("Имя предмета обновлено на {}.", name);
            existingItem.setName(name);
        }

        if (description != null) {
            log.info("Описание предмета обновлено на {}.", description);
            existingItem.setDescription(description);
        }

        if (available != null && available != existingItem.getAvailable()) {
            log.info("Статус предмета обновлён на {}.", available);
            existingItem.setAvailable(available);
        }

        return ItemMapper.toItemDto(itemRepository.updateItem(existingItem, itemId, userId));
    }
}
