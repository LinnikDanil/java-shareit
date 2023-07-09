package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    void deleteUser(long userId);
}
