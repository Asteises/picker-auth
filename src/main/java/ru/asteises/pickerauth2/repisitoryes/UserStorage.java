package ru.asteises.pickerauth2.repisitoryes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.asteises.pickerauth2.model.User;

import java.util.Optional;

@Repository
public interface UserStorage extends JpaRepository<User, String> {

    Optional<User> findByLogin(String login);
}
