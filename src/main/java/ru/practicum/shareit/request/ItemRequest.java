package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private long id;
    private User user;
    private String description;
    private LocalDateTime created;
}
