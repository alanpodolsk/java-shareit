package ru.practicum.request;

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

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return null;
    }


    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return null;
    }


    public List<ItemRequestDto> getAllRequests(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return null;
    }


    public ItemRequestDto getRequest(@PathVariable Integer requestId, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return null;
    }
}
