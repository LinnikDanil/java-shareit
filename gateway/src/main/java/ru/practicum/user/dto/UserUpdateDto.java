package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private long id;

    private String name;

    @Email(message = "Неверный формат электронной почты")
    private String email;
}