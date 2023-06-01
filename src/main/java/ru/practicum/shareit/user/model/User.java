package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class User {
    private long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @NotNull
    @Email(message = "Неверный формат электронной почты")
    private String email;
}
