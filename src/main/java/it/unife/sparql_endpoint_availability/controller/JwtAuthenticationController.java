package it.unife.sparql_endpoint_availability.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.unife.sparql_endpoint_availability.jwt.JwtConfig;
import it.unife.sparql_endpoint_availability.jwt.TokenManager;
import it.unife.sparql_endpoint_availability.jwt.UsernamePasswordAuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/login")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    private final SecretKey secretKey;

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationController.class);

    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtConfig jwtConfig, SecretKey secretKey)
    {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String login(HttpServletRequest request) throws IOException {

            UsernamePasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernamePasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword());


            Authentication authResult = authenticationManager.authenticate(authentication);
            logger.info("Authentication successful for user: " + authenticationRequest.getUsername());


         String token = TokenManager.createToken(authResult.getName(),
                authResult.getAuthorities(),
                jwtConfig.getExpirationTimeAfertDays(),
                secretKey);

        //retur token json
        return jwtConfig.getPrefix() + token;
    }
}
