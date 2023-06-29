package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.exception.BookingValidationException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemOwnerIsDefferentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repositrory.CommentRepository;
import ru.practicum.shareit.item.repositrory.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    private final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@mail.ru")
            .build();

    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@mail.ru")
            .build();

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAllUsers() {
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> userDtos = userService.getAllUsers();

        assertEquals(2, userDtos.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersWithEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(
                UserNotFoundException.class, () -> userService.getAllUsers());

        String expectedMessage = "Список пользователей пуст";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto userDto = userService.getUserById(user1.getId());

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getName(), userDto.getName());
        assertEquals(user1.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void getUserByIdWithNonExistentUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class, () -> userService.getUserById(10L));

        String expectedMessage = String.format("Пользователь с id = %s не найден", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveUser() {
        UserDto userDto = UserMapper.toUserDto(user1);
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto savedUserDto = userService.saveUser(userDto);

        assertEquals(user1.getId(), savedUserDto.getId());
        assertEquals(user1.getName(), savedUserDto.getName());
        assertEquals(user1.getEmail(), savedUserDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setName("updatedName");
        userDto.setEmail("updatedEmail@mail.ru");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user1));

        UserDto updatedUserDto = userService.updateUser(userDto, user1.getId());

        assertEquals(userDto.getName(), updatedUserDto.getName());
        assertEquals(userDto.getEmail(), updatedUserDto.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserWithNonExistentUser() {
        UserDto userDto = UserMapper.toUserDto(user1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                UserNotFoundException.class, () -> userService.updateUser(userDto, 10L));

        String expectedMessage = String.format("Пользователь с id = %s не найден", 10L);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateUserWithBlankName() {
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setName("");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        Exception exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(userDto, user1.getId()));

        String expectedMessage = "Имя пользователя не может быть пустым";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateUserWithExistingEmail() {
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setEmail(user2.getEmail());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        Exception exception = assertThrows(
                UserAlreadyExistException.class, () -> userService.updateUser(userDto, user1.getId()));

        String expectedMessage = String.format("Email %s уже существует.", userDto.getEmail());
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.deleteUser(user1.getId());

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}