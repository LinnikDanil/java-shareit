package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestResponseFullDto> getOwnerRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseFullDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseFullDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
