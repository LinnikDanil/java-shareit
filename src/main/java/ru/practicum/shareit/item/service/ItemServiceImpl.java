package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIsDefferentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
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

    @Transactional
    @Override
    public List<ItemDto> getUserItems(long userId) {
        log.info("Вывод всех предметов пользователя с id = {}:", userId);

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));
        //Проверка на существование пользователя

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.info("Вывод предмета с id = {}:", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(
                String.format("Предмета с id = %s не существует", itemId)));

        return ItemMapper.toItemDto(item);
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
}
