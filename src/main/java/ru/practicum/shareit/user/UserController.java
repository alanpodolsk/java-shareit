package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id){
    return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers(){
       return userService.getAllUsers();
    }

    @PatchMapping
    public UserDto updateUser(User user){
        return userService.updateUser(user);
    }

    @PostMapping
    public UserDto createUser(User user){
        return userService.createUser(user);
    }

    @DeleteMapping ("/{id}")
    public void deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
    }
}
