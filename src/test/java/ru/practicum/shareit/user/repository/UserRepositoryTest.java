package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void clearRepository() {
        userRepository.deleteAll();
    }

    @Test
    public void findByEmailTest() {
        User user = userRepository.save(new User(null, "name", "email@email.ru"));
        User findUser = userRepository.findByEmail("email@email.ru").get();

        assertEquals(findUser.getId(), user.getId());
        assertEquals(findUser.getName(), user.getName());
        assertEquals(findUser.getEmail(), user.getEmail());
    }
}