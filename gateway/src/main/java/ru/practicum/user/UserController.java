package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Create;
import ru.practicum.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer id) {
        return userService.updateUser(userDto, id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Validated(Create.class) UserDto user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
