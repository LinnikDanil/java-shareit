package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User ownerRequest = User.builder()
            .name("ownerRequest")
            .email("ownerRequest@mail.ru")
            .build();

    private ItemRequest itemRequest1 = ItemRequest.builder()
            .user(ownerRequest)
            .description("Нужна газонокосилка.")
            .created(LocalDateTime.now().plusHours(1))
            .build();

    private ItemRequest itemRequest2 = ItemRequest.builder()
            .user(ownerRequest)
            .description("Нужна бензопила.")
            .created(LocalDateTime.now())
            .build();

    @AfterEach
    void clearRepository() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @BeforeEach
    void saveEntity() {
        userRepository.save(ownerRequest);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);

    }

    @Test
    void findAllByUserIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdOrderByCreatedDesc(ownerRequest.getId());

        assertEquals(itemRequests.size(), 2);
        assertEquals(itemRequests.get(0).getId(), itemRequest1.getId());
        assertEquals(itemRequests.get(0).getUser(), itemRequest1.getUser());
        assertEquals(itemRequests.get(0).getCreated(), itemRequest1.getCreated());
        assertEquals(itemRequests.get(0).getDescription(), itemRequest1.getDescription());

        assertEquals(itemRequests.get(1).getId(), itemRequest2.getId());
        assertEquals(itemRequests.get(1).getUser(), itemRequest2.getUser());
        assertEquals(itemRequests.get(1).getCreated(), itemRequest2.getCreated());
        assertEquals(itemRequests.get(1).getDescription(), itemRequest2.getDescription());
    }

    @Test
    void findAllByUserIdNot() {
        User owner2 = User.builder()
                .name("ownerRequest2")
                .email("ownerRequest2@mail.ru")
                .build();
        userRepository.save(owner2);
        itemRequest2.setUser(owner2);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByUserIdNot(
                owner2.getId(), Pageable.unpaged()).getContent();

        assertEquals(itemRequests.size(), 1);
        assertEquals(itemRequests.get(0).getId(), itemRequest1.getId());
        assertEquals(itemRequests.get(0).getUser(), itemRequest1.getUser());
        assertEquals(itemRequests.get(0).getCreated(), itemRequest1.getCreated());
        assertEquals(itemRequests.get(0).getDescription(), itemRequest1.getDescription());
    }
}