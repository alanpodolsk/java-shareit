package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.InputItemDto;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.item.model.Comment;

import java.util.List;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public OutputItemDto createItem(InputItemDto inputItemDto, Integer userId) {
        return null;
    }


    public CommentDto createComment(Comment comment, Integer userId, Integer itemId) {
        return null;
    }


    public OutputItemDto updateItem(InputItemDto inputItemDto, Integer userId, Integer itemId) {
        return null;
    }


    public OutputItemDto getItem(Integer itemId, Integer userId) {
        return null;
    }


    public List<OutputItemDto> getItemsByUser(Integer userId, Integer from, Integer size) {
        return null;

    }


    public List<OutputItemDto> getItemsBySearch(String text, Integer from, Integer size) {
        return null;
    }
}
