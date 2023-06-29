package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentMapperTest {
    @Test
    public void testToCommentDto() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("First comment");
        comment1.setAuthor(new User());
        comment1.setCreated(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Second comment");
        comment2.setAuthor(new User());
        comment2.setCreated(LocalDateTime.now());

        List<Comment> comments = Arrays.asList(comment1, comment2);

        List<CommentDto> result = CommentMapper.toCommentDto(comments);

        assertNotNull(result);
        assertEquals(2, result.size());

        CommentDto commentDto1 = result.get(0);
        assertEquals(comment1.getId(), commentDto1.getId());
        assertEquals(comment1.getText(), commentDto1.getText());
        assertEquals(comment1.getCreated(), commentDto1.getCreated());

        CommentDto commentDto2 = result.get(1);
        assertEquals(comment2.getId(), commentDto2.getId());
        assertEquals(comment2.getText(), commentDto2.getText());
        assertEquals(comment2.getCreated(), commentDto2.getCreated());
    }
}
