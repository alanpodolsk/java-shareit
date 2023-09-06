package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public User updateUser(@RequestBody UserDto user, @PathVariable Integer id) {
        return userService.updateUser(user, id);
    }

    @PostMapping
    public User createUser(@RequestBody UserDto user) {
        return userService.createUser(UserMapper.toUser(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
