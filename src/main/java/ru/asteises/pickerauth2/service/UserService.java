package ru.asteises.pickerauth2.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.asteises.pickerauth2.model.User;
import ru.asteises.pickerauth2.model.UserRegistrationDto;
import ru.asteises.pickerauth2.repisitoryes.UserStorage;

import javax.security.auth.message.AuthException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(8);

    public User getByLogin(@NonNull String login) throws AuthException {

        return userStorage.findByLogin(login)
                .orElseThrow(() -> new AuthException("Пользователь не найден"));
    }

    public void registration(UserRegistrationDto userRegistrationDto) {

        User user = User.builder()
                .firstName(userRegistrationDto.getFirstName())
                .lastName(userRegistrationDto.getLastName())
                .login(userRegistrationDto.getLogin())
                .roles(userRegistrationDto.getRoles())
                .password(encoder.encode(userRegistrationDto.getPassword()))
                .build();

        userStorage.save(user);
    }
}
