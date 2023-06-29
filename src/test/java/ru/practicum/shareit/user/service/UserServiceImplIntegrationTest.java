package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserServiceImpl userService;

    private final User user1 = User.builder().name("user1").email("user1@mail.ru").build();
    private final User user2 = User.builder().name("user2").email("user2@mail.ru").build();

    @BeforeEach
    void setUp() {
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void updateUser() {
        String newName = "updatedUser1";
        String newEmail = "updatedUser1@mail.ru";

        UserDto userDto = new UserDto();
        userDto.setName(newName);
        userDto.setEmail(newEmail);

        UserDto updatedUser = userService.updateUser(userDto, user1.getId());

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newEmail, updatedUser.getEmail());

        UserDto nonUpdatedUser = userService.getUserById(user2.getId());

        assertNotNull(nonUpdatedUser);
        assertNotEquals(newName, nonUpdatedUser.getName());
        assertNotEquals(newEmail, nonUpdatedUser.getEmail());
    }
}
