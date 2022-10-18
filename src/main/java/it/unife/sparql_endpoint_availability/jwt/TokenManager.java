package it.unife.sparql_endpoint_availability.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.LocalDate;
import java.util.Base64;
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

    public static String encodeToken(String token, String key) {

        byte[] encrypted = new byte[0];

        try {
            key = cutKeyTo16Bytes(key);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encrypted = cipher.doFinal(token.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decodeToken(String token, String key) {

        byte[] decrypted = new byte[0];

        try {
            key = cutKeyTo16Bytes(key);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decrypted = cipher.doFinal(Base64.getDecoder().decode(token));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }

    private static String cutKeyTo16Bytes(String key) {
        if (key.length() > 16) {
            return key.substring(0, 16);
        } else if (key.length() < 16) {
            return key + "0000000000000000".substring(0, 16 - key.length());
        }
        return key;
    }
}
