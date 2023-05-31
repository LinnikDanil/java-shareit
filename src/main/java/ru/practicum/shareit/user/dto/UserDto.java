package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = CreateValidationGroup.class)
    private String name;

    @NotNull(groups = CreateValidationGroup.class)
    @Email(message = "Неверный формат электронной почты", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String email;
}

