package ru.asteises.pickerauth2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.asteises.pickerauth2.jwt.JwtAuthentication;
import ru.asteises.pickerauth2.model.UserRegistrationDto;
import ru.asteises.pickerauth2.service.AuthService;
import ru.asteises.pickerauth2.service.UserService;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiExampleController {

    private final AuthService authService;

    private final UserService userService;

    @PostMapping
    public ResponseEntity<String> registration(UserRegistrationDto userRegistrationDto) {

        userService.registration(userRegistrationDto);

        return ResponseEntity.ok("User create");
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("hello/user")
    public ResponseEntity<String> helloUser() {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        return ResponseEntity.ok("Hello user " + authInfo.getPrincipal() + "!");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("hello/admin")
    public ResponseEntity<String> helloAdmin() {
        final JwtAuthentication authInfo = authService.getAuthInfo();
        return ResponseEntity.ok("Hello admin " + authInfo.getPrincipal() + "!");
    }

}
