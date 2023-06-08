package ru.practicum.shareit.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class ItemRequest {
    private Long id;
    private User user;
    private String description;
    private LocalDateTime created;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemRequest)) return false;
        return id != null && id.equals(((ItemRequest) obj).getId());
    }
}
