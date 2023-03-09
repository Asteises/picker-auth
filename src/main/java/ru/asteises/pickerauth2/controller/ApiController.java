package ru.asteises.pickerauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.asteises.pickerauth2.jwt.JwtAuthentication;
import ru.asteises.pickerauth2.jwt.JwtRequest;
import ru.asteises.pickerauth2.jwt.JwtResponse;
import ru.asteises.pickerauth2.model.UserRegistrationDto;
import ru.asteises.pickerauth2.service.AuthService;
import ru.asteises.pickerauth2.service.UserService;

import javax.security.auth.message.AuthException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("user/registration")
    public ResponseEntity<JwtResponse> registration(UserRegistrationDto userRegistrationDto) throws AuthException {

        JwtRequest jwtRequest = new JwtRequest(userRegistrationDto.getLogin(), userRegistrationDto.getPassword());

        userService.registration(userRegistrationDto);

        return ResponseEntity.ok(authService.login(jwtRequest));
    }

}
