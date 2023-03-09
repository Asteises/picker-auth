package ru.asteises.pickerauth2.service;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.asteises.pickerauth2.jwt.JwtAuthentication;
import ru.asteises.pickerauth2.model.Role;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {

        final JwtAuthentication jwtInfoToken = new JwtAuthentication();

        jwtInfoToken.setRoles(getRoles(claims));
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());

        return jwtInfoToken;
    }

    // todo вот тут непонятно!
    private static Set<Role> getRoles(Claims claims) {

        final List<LinkedHashMap<String, String>> roles = claims.get("roles", List.class);

        return Set.of(new Role(roles.get(0).get("id"), roles.get(0).get("name")));
    }

}
