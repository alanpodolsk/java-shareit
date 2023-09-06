package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NoObjectException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public User getUser(Integer Id) {
        User user = userRepository.users.get(Id);
        if (user != null){
            return user;
        }
        return null;
    };

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.users.values());
    }

    @Override
    public User createUser(User user) {
        if (user != null){
            checkEmail(user.getEmail());
            user.setId(UserRepository.currentId + 1);
            UserRepository.currentId = user.getId();
            UserRepository.users.put(user.getId(),user);
            UserRepository.uniqueEmails.add(user.getEmail());
            return UserRepository.users.get(user.getId());
        } else {
            throw new NoObjectException("Объект пользователя не может быть null");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        User user = UserRepository.users.get(id);
        if (user == null){
            return;
        }
        UserRepository.uniqueEmails.remove(user.getEmail());
        UserRepository.users.remove(id);
    }

    @Override
    public User updateUser(UserDto userDto, Integer id) {
        if (userDto != null){
            User user = UserRepository.users.get(id);
            if (user != null){
                if (userDto.getName() != null){user.setName(userDto.getName());}
                if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())){
                    checkEmail(userDto.getEmail());
                    String oldEmail = user.getEmail();
                    user.setEmail(userDto.getEmail());
                    UserRepository.uniqueEmails.remove(oldEmail);
                    UserRepository.uniqueEmails.add(user.getEmail());
                }
                UserRepository.users.put(user.getId(),user);
                return UserRepository.users.get(user.getId());
            } else {
                throw new NoObjectException("Пользователя с данным id не существует в программе");
            }
        } else {
            throw new NoObjectException("Объект пользователя не может быть null");
        }
    }
    private void checkEmail(String email){
        if (email == null){
            throw new ValidationException("Email не может быть пустым");
        } else if (UserRepository.uniqueEmails.contains(email)){
            throw new ConflictException("Email должен быть уникальным");
        } else if (!email.contains("@")){
            throw new ValidationException("Неверный формат Email");
        }
    }
}
