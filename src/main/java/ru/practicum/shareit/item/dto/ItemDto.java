package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean isAvailable;
    private User owner;
    private String request;
}
