package ru.asteises.pickerauth2.service;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.asteises.pickerauth2.jwt.JwtAuthentication;
import ru.asteises.pickerauth2.jwt.JwtProvider;
import ru.asteises.pickerauth2.jwt.JwtRequest;
import ru.asteises.pickerauth2.jwt.JwtResponse;
import ru.asteises.pickerauth2.model.User;

import javax.security.auth.message.AuthException;
import java.util.HashMap;
import java.util.Map;

/*
Вы могли заметить, что мы не валидируем пароль пользователя. Для проверки пароля мы создадим отдельный сервис
AuthService, который также будет отвечать за получение новых access и refresh токенов взамен протухающим.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    /*
    Для хранения рефреш токена используется HashMap лишь для упрощения примера. Лучше использовать какое-нибудь
    постоянное хранилище, например Redis.

    Зачем сохранять токены?

    Сохранять их не обязательно, но это дает некоторые преимущества. Если каким-то образом злоумышленник заполучит
    секретный ключ для генерации refresh токенов, он не сможет создавать токены. Потому что ему нужно будет знать время
    создания токена конкретным пользователем.

    Если бы сохранения не было, то он бы мог сгенерировать любой токен для любого пользователя, получить по нему access
    токены, и творить беспредел в системе.

    Но учтите, если у вас есть сайт и мобильное приложение, то вам нужно будет сохранять два refresh токена для одного
    пользователя. По одному на каждого клиента API.

    Еще один плюс, вы можете забанить пользователя в системе и отозвать его refresh токен, реализовав удаление токена
    из хранилища сохраненных и запрет на выдачу новых забаненым пользователям. В другом случае пользователь смог бы
    выпускать себе новые access токены, пока не протух бы refresh токен.
     */
    private final Map<String, String> refreshStorage = new HashMap<>();

    private final JwtProvider jwtProvider;

    /*
    Сначала мы находим пользователя по логину. Если пользователь найден, и присланный пароль совпадает с паролем
    пользователя, то передаем объект пользователя в JwtProvider и получаем от него токены. Далее сохраняем выданный
    рефреш токен в мапу refreshStorage и возвращаем объект JwtResponse с токенами. Зачем сохранять рефреш токены, я
    объясню позже.
     */
    public JwtResponse login(@NonNull JwtRequest authRequest) throws AuthException {

        final User user = userService.getByLogin(authRequest.getLogin());

        if (user.getPassword().equals(authRequest.getPassword())) {

            final String accessToken = jwtProvider.generateAccessToken(user);

            final String refreshToken = jwtProvider.generateRefreshToken(user);

            refreshStorage.put(user.getLogin(), refreshToken);

            return new JwtResponse(accessToken, refreshToken);
        } else {

            throw new AuthException("Неправильный пароль");
        }
    }

    /*
    Метод принимает refresh токен, а возвращает новый access токен. Сначала мы проверяем, что присланный rehresh токен
    валиден. Если валиден, то получаем claims и оттуда получаем логин пользователя. Далее по логину находим выданный
    пользователю refresh токен в мапе refreshStorage, и сверяем его с присланным пользователем. Если токены одинаковые,
    то получаем объект User, который отправляем в JwtProvider и получаем новый access токен, без обновления refresh
    токена.
     */
    public JwtResponse getAccessToken(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getByLogin(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final User user = userService.getByLogin(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
