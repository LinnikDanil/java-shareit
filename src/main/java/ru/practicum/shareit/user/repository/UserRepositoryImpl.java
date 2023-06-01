package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users;
    private int id = 0;

    @Override
    public List<User> getAllUsers() {
        if (users.values().isEmpty()) {
            log.warn("Список пользователей пуст.");
            throw new UserNotFoundException("Список пользователей пуст");
        }
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long userId) {
        if (users.isEmpty()) {
            log.warn("Список пользователей пуст.");
            throw new UserNotFoundException("Список пользователей пуст");
        }
        User user = users.get(userId);
        if (user == null) {
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }
        return user;
    }

    @Override
    public User saveNewUser(User user) {
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException(String.format("Email %s уже существует.", user.getEmail()));
            }
        }

        user.setId(generatedNewId());
        users.put(user.getId(), user);
        log.info("Пользователь сохранён с id = {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователя с id = {} не существует.", user.getId());
            throw new UserNotFoundException(String.format("Пользователь с id = %s не найден", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} обновлён.", user.getId());
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id = %s не найден", userId));
        }
        users.remove(userId);
        log.info("Пользователь с id = {} удалён.", userId);
    }

    private long generatedNewId() {
        return ++id;
    }
}
