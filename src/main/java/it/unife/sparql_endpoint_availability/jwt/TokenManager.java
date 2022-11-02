package it.unife.sparql_endpoint_availability.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import java.security.Key;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class TokenManager {

    public static String createToken(String username, Collection<? extends GrantedAuthority> authorities,
            int expirationAfterDays, Key secret) {
        return Jwts.builder()
                .setSubject(username) // name of user
                .claim("authorities", authorities) // authorities of user
                .setIssuedAt(new Date()) // date of creation
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(expirationAfterDays))) // date of
                                                                                                     // expiration
                .signWith(secret) // sign with key
                .compact();
    }
}
