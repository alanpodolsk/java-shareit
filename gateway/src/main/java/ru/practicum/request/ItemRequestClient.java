package ru.practicum.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.BaseClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.request.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemRequestValidations.validateCreateRequest(itemRequestDto, userId);
        return post("",userId,itemRequestDto);
    }


    public ResponseEntity<Object> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemRequestValidations.validateGetRequestsByUser(userId);
        return get("",userId);
    }


    public ResponseEntity<Object> getAllRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemRequestValidations.validateGetAllRequests(from, size, userId);
        return get("/all?from={from}&size={size}", Long.valueOf(userId), Map.of("from",from,"size",size));
    }


    public ResponseEntity<Object> getRequest(@PathVariable Integer requestId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemRequestValidations.validateGetRequest(requestId, userId);
        return get("/"+requestId,userId);
    }
}
