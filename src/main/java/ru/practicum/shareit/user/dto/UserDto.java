package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.validation.CreateValidationGroup;
import ru.practicum.shareit.user.dto.validation.UpdateValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = CreateValidationGroup.class)
    private String name;

    @NotNull(groups = CreateValidationGroup.class)
    @Email(message = "Неверный формат электронной почты", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    private String email;
}

