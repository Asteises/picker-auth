package ru.asteises.pickerauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.asteises.pickerauth2.service.AuthService;
import ru.asteises.pickerauth2.jwt.JwtRequest;
import ru.asteises.pickerauth2.jwt.JwtResponse;
import ru.asteises.pickerauth2.jwt.RefreshJwtRequest;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
    Эндпойнт /api/auth/login принимает JwtRequest, а возвращает JwtResponse с токенами.
     */
    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws AuthException {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    /*
    Эндпойнт /api/auth/token  принимает RefreshJwtRequest c единственным полем refreshToken и возвращает JwtResponse с
    новым access токеном.
     */
    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    /*
    Эндпойнт /api/auth/refresh  принимает RefreshJwtRequest  и возвращает JwtResponse с новыми токенами.
     */
    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) throws AuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
