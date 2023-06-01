package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    User getUserById(long userId);

    User saveNewUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);
}
