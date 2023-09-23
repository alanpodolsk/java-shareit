package ru.practicum.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findByEmail(String email);
}
