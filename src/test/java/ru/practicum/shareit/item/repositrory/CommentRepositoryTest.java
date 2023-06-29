package ru.practicum.shareit.item.repositrory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private User owner = User.builder()
            .name("owner")
            .email("owner@mail.ru")
            .build();

    private User author = User.builder()
            .name("author")
            .email("author@mail.ru")
            .build();
    private Item item1 = Item.builder()
            .name("Бензопила")
            .description("Аккумуляторная бензопила")
            .available(true)
            .owner(owner)
            .build();
    private Item item2 = Item.builder()
            .name("Газонокосилка")
            .description("Аккумуляторная газонокосилка")
            .available(true)
            .owner(owner)
            .build();

    private Comment comment1 = Comment.builder()
            .text("Класс!")
            .item(item1)
            .author(author)
            .created(LocalDateTime.now())
            .build();
    private Comment comment2 = Comment.builder()
            .text("Супер!")
            .item(item2)
            .author(author)
            .created(LocalDateTime.now())
            .build();

    @AfterEach
    void clearRepository() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @BeforeEach
    void saveEntity() {
        userRepository.save(author);
        userRepository.save(owner);
        itemRepository.save(item1);
        itemRepository.save(item2);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }
    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());

        assertEquals(comments.size(), 1);
        assertEquals(comments.get(0).getId(), comment1.getId());
        assertEquals(comments.get(0).getText(), comment1.getText());
        assertEquals(comments.get(0).getCreated(), comment1.getCreated());
        assertEquals(comments.get(0).getAuthor(), comment1.getAuthor());
        assertEquals(comments.get(0).getItem(), comment1.getItem());
    }

    @Test
    void findAllByItemIdIn() {
        List<Comment> comments = commentRepository.findAllByItemIdIn(List.of(item1.getId(), item2.getId()));

        assertEquals(comments.size(), 2);

        assertEquals(comments.get(0).getId(), comment1.getId());
        assertEquals(comments.get(0).getText(), comment1.getText());
        assertEquals(comments.get(0).getCreated(), comment1.getCreated());
        assertEquals(comments.get(0).getAuthor(), comment1.getAuthor());
        assertEquals(comments.get(0).getItem(), comment1.getItem());

        assertEquals(comments.get(1).getId(), comment2.getId());
        assertEquals(comments.get(1).getText(), comment2.getText());
        assertEquals(comments.get(1).getCreated(), comment2.getCreated());
        assertEquals(comments.get(1).getAuthor(), comment2.getAuthor());
        assertEquals(comments.get(1).getItem(), comment2.getItem());
    }
}