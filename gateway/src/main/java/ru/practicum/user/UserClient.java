package ru.practicum.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.BaseClient;
import ru.practicum.Create;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public UserDto getUser(Integer id) {
        return null;
    }


    public List<UserDto> getAllUsers() {
        return null;
    }


    public UserDto updateUser(UserDto userDto, Integer id) {
        return null;
    }


    public UserDto createUser(UserDto user) {
        return null;
    }
}
