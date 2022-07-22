package it.unife.sparql_endpoint_availability.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    //logger
    private final static Logger logger = LoggerFactory.getLogger(JwtUsernamePasswordAuthenticationFilter.class);




    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig, SecretKey secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    /*
        * This method is called when a user sends credentials to the server.
        * We get the username and password, and then we call the authentication manager
        * to validate the credentials
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            UsernamePasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernamePasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword());

            logger.info("Attempting to authenticate user: " + authenticationRequest.getUsername());

            Authentication authenticate = authenticationManager.authenticate(authentication);
            logger.info("Authentication successful for user: " + authenticationRequest.getUsername());
            return authenticate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        * This method is called when the user is successfully authenticated.
        * We generate a token and return it to the user
    */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(authResult.getName()) //name of user
                .claim("authorities", authResult.getAuthorities()) //authorities of user
                .setIssuedAt(new Date()) //date of creation
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getExpirationTimeAfertDays()))) //date of expiration
                .signWith(secretKey) //sign with key
                .compact();

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getPrefix() + token);
    }
}


