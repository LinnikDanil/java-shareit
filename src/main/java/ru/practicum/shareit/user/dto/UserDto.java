package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private long id;

    private String name;

    @Email(message = "Неверный формат электронной почты")
    private String email;
}