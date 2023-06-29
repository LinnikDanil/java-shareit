package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, long userId) {
        log.info("Создание запроса предмета пользователем с id = {}.", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestResponseDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseFullDto> getOwnerRequests(long userId) {
        log.info("Вывод всех запросов владельца с id = {}.", userId);

        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не найден", userId)));

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userId);

        return toFullItemRequestResponseDto(itemRequests);
    }

    @Override
    public List<ItemRequestResponseFullDto> getUserRequests(long userId, int from, int size) {
        log.info("Вывод всех запросов пользователя с id = {}, начиная с {}, выводя по {}.", userId, from, size);

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByUserIdNot(userId, pageable);
        List<ItemRequest> itemRequests = itemRequestPage.getContent();

        return toFullItemRequestResponseDto(itemRequests);
    }

    @Override
    public ItemRequestResponseFullDto getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("Пользователь с id = %s не имеет права искать запросы", userId)));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new ItemRequestNotFoundException(String.format("Запрос на предмет с id = %d не найден.", requestId)));
        List<ItemDto> itemsForRequestDto = ItemMapper.toItemDto(itemRepository.findAllByRequest(requestId));
        return ItemRequestMapper.toItemRequestResponseFullDto(itemRequest, itemsForRequestDto);
    }

    private List<ItemRequestResponseFullDto> toFullItemRequestResponseDto(List<ItemRequest> itemRequests) {
        return itemRequests.isEmpty() ? Collections.emptyList() : itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findAllByRequest(itemRequest.getId());
                    List<ItemDto> itemsDto = ItemMapper.toItemDto(items);
                    return ItemRequestMapper.toItemRequestResponseFullDto(itemRequest, itemsDto);
                })
                .collect(Collectors.toList());
    }
}
