package ru.practicum.shareit.item.repositrory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User ownerItem = User.builder()
            .name("ownerItem")
            .email("ownerItem@mail.ru")
            .build();

    private User ownerRequest = User.builder()
            .name("ownerRequest")
            .email("ownerRequest@mail.ru")
            .build();

    private ItemRequest itemRequest = ItemRequest.builder()
            .user(ownerRequest)
            .description("Нужна газонокосилка.")
            .created(LocalDateTime.now())
            .build();

    private Item item1 = Item.builder()
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(ownerItem)
            .build();
    private Item item2 = Item.builder()
            .name("Газонокосилка")
            .description("Аккумуляторная газонокосилка")
            .available(true)
            .owner(ownerItem)
            .build();

    @AfterEach
    void clearRepository() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @BeforeEach
    void saveEntity() {
        userRepository.save(ownerItem);
        userRepository.save(ownerRequest);
        itemRequestRepository.save(itemRequest);
        itemRepository.save(item1);
        item2.setRequest(itemRequest.getId());
        itemRepository.save(item2);
    }

    @Test
    void findAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(ownerItem.getId(), Pageable.unpaged()).getContent();

        assertEquals(items.size(), 2);
        assertEquals(items.get(0).getId(), item1.getId());
        assertEquals(items.get(0).getOwner(), item1.getOwner());
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(0).getDescription(), item1.getDescription());
        assertEquals(items.get(0).getAvailable(), item1.getAvailable());

        assertEquals(items.get(1).getId(), item2.getId());
        assertEquals(items.get(1).getOwner(), item2.getOwner());
        assertEquals(items.get(1).getRequest(), item2.getRequest());
        assertEquals(items.get(1).getName(), item2.getName());
        assertEquals(items.get(1).getDescription(), item2.getDescription());
        assertEquals(items.get(1).getAvailable(), item2.getAvailable());
    }

    @Test
    void search() {
        List<Item> items = itemRepository.search("Бенз", Pageable.unpaged()).getContent();

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item1.getId());
        assertEquals(items.get(0).getOwner(), item1.getOwner());
        assertEquals(items.get(0).getName(), item1.getName());
        assertEquals(items.get(0).getDescription(), item1.getDescription());
        assertEquals(items.get(0).getAvailable(), item1.getAvailable());

        List<Item> items2 = itemRepository.search("Аккум", Pageable.unpaged()).getContent();
        assertEquals(items2.size(), 2);
        assertEquals(items2.get(0).getId(), item1.getId());
        assertEquals(items2.get(0).getOwner(), item1.getOwner());
        assertEquals(items2.get(0).getName(), item1.getName());
        assertEquals(items2.get(0).getDescription(), item1.getDescription());
        assertEquals(items2.get(0).getAvailable(), item1.getAvailable());

        assertEquals(items2.get(1).getId(), item2.getId());
        assertEquals(items2.get(1).getOwner(), item2.getOwner());
        assertEquals(items2.get(1).getRequest(), item2.getRequest());
        assertEquals(items2.get(1).getName(), item2.getName());
        assertEquals(items2.get(1).getDescription(), item2.getDescription());
        assertEquals(items2.get(1).getAvailable(), item2.getAvailable());
    }

    @Test
    void findAllByRequest() {
        List<Item> items = itemRepository.findAllByRequest(itemRequest.getId());

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getId(), item2.getId());
        assertEquals(items.get(0).getOwner(), item2.getOwner());
        assertEquals(items.get(0).getName(), item2.getName());
        assertEquals(items.get(0).getDescription(), item2.getDescription());
        assertEquals(items.get(0).getAvailable(), item2.getAvailable());
        assertEquals(items.get(0).getRequest(), item2.getRequest());
    }
}