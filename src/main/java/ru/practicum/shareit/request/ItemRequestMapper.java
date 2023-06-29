package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(null, user, itemRequestDto.getDescription(), LocalDateTime.now());
    }

    public ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        return new ItemRequestResponseDto(
                itemRequest.getId(),
                itemRequest.getUser(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public ItemRequestResponseFullDto toItemRequestResponseFullDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestResponseFullDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}
