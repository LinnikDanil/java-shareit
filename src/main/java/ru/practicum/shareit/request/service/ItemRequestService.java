package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto createRequest(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestResponseFullDto> getOwnerRequests(long userId);

    List<ItemRequestResponseFullDto> getUserRequests(long userId, int from, int size);

    ItemRequestResponseFullDto getRequestById(Long requestId, Long userId);
}
