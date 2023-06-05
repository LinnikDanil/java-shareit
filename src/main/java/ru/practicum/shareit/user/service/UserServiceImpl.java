package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Вывод всех пользователей:");
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            log.warn("Список пользователей пуст.");
            throw new UserNotFoundException("Список пользователей пуст");
        }

        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Вывод пользователя с id = {}:", userId);
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()){
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }

        return UserMapper.toUserDto(optUser.get());
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Сохранение пользователя:");

//        Optional<User> optExistingUser = userRepository.findByEmail(userDto.getEmail());
//        if (optExistingUser.isPresent()) {
//            log.warn("Пользователь с email = {} уже существует.", userDto.getEmail());
//            throw new UserAlreadyExistException(String.format("Email %s уже существует.", userDto.getEmail()));
//        }

        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Пользователь сохранён с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Обновление пользователя:");
        Optional<User> optExistingUser = userRepository.findById(userId);

        if (optExistingUser.isEmpty()){
            log.warn("Пользователя с id = {} не существует.", userId);
            throw new UserNotFoundException(String.format("Пользователя с id = %s не существует", userId));
        }

        User existingUser = optExistingUser.get();

        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new IllegalArgumentException("Имя пользователя не может быть пустым");
            } else {
                existingUser.setName(userDto.getName());
                log.info("Имя пользователя обновлено на {}.", userDto.getName());
            }
        }

        if (userDto.getEmail() != null) {
            for (User user : userRepository.findAll()) {
                if (user.getEmail().equals(userDto.getEmail()) && user.getId() != userId) {
                    throw new UserAlreadyExistException(String.format("Email %s уже существует.", userDto.getEmail()));
                }
            }
            existingUser.setEmail(userDto.getEmail());
            log.info("Почта пользователя обновлена на {}.", userDto.getEmail());
        }

        userRepository.save(existingUser);
        return UserMapper.toUserDto(existingUser);
    }


    @Override
    public void deleteUser(long userId) {
        log.info("Удаление пользователя с id = {}:", userId);
        userRepository.deleteById(userId);
    }
}
