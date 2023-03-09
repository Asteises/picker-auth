package ru.asteises.pickerauth2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserRegistrationDto {

    private String login;
    // todo как правильно получить пароль от фронта?
    private String password;
    private String firstName;
    private String lastName;
    private Set<Role> roles;

}
