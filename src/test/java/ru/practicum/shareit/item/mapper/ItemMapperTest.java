package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperTest {
    @Test
    public void testToItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");
        item.setDescription("Description1");
        item.setAvailable(true);
        item.setRequest(2L);

        ItemDto result = ItemMapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest(), result.getRequestId());
    }

    @Test
    public void testToItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item1");
        itemDto.setDescription("Description1");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);

        Item result = ItemMapper.toItem(itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequest());
    }

    @Test
    public void testToItemDtoList() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item1");
        item1.setDescription("Description1");
        item1.setAvailable(true);
        item1.setRequest(2L);

        Item item2 = new Item();
        item2.setId(3L);
        item2.setName("Item2");
        item2.setDescription("Description2");
        item2.setAvailable(false);
        item2.setRequest(4L);

        List<Item> items = Arrays.asList(item1, item2);

        List<ItemDto> result = ItemMapper.toItemDto(items);

        assertNotNull(result);
        assertEquals(2, result.size());

        ItemDto dto1 = result.get(0);
        assertEquals(item1.getId(), dto1.getId());
        assertEquals(item1.getName(), dto1.getName());
        assertEquals(item1.getDescription(), dto1.getDescription());
        assertEquals(item1.getAvailable(), dto1.getAvailable());
        assertEquals(item1.getRequest(), dto1.getRequestId());

        ItemDto dto2 = result.get(1);
        assertEquals(item2.getId(), dto2.getId());
        assertEquals(item2.getName(), dto2.getName());
        assertEquals(item2.getDescription(), dto2.getDescription());
        assertEquals(item2.getAvailable(), dto2.getAvailable());
        assertEquals(item2.getRequest(), dto2.getRequestId());
    }
}
