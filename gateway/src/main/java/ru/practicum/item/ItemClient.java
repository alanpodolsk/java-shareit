package ru.practicum.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.InputItemDto;
import ru.practicum.item.dto.OutputItemDto;
import ru.practicum.item.model.Comment;
import ru.practicum.item.model.Item;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createItem(InputItemDto inputItemDto, Integer userId) {
        ItemValidations.validateCreateItem(inputItemDto,userId);
        return post("",userId,inputItemDto);
    }


    public ResponseEntity<Object> createComment(Comment comment, Integer userId, Integer itemId) {
        ItemValidations.validateCreateComment(comment,userId,itemId);
        return post("/"+itemId+"/comment",userId,comment);
    }


    public ResponseEntity<Object> updateItem(InputItemDto inputItemDto, Integer userId, Integer itemId) {
        ItemValidations.validateUpdateItem(inputItemDto,userId,itemId);
        return patch("/"+itemId,userId,inputItemDto);
    }


    public ResponseEntity<Object> getItem(Integer itemId, Integer userId) {
        ItemValidations.validateGetItem(itemId, userId);
        return get("/"+itemId,userId);
    }


    public ResponseEntity<Object> getItemsByUser(Integer userId, Integer from, Integer size) {
        ItemValidations.validateGetItemsByUser(userId,from,size);
        return get("", Long.valueOf(userId), Map.of("start",from,"size",size));
    }


    public ResponseEntity<Object> getItemsBySearch(String text, Integer from, Integer size) {
        ItemValidations.validateGetItemsBySearch(text,from,size);
        return rest.getForEntity("/search?text={text}&from={from}&size={size}",Object.class,Map.of("text",text,"from",from,"size",size));
    }
}
