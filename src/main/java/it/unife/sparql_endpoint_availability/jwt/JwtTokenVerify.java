package it.unife.sparql_endpoint_availability.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import it.unife.sparql_endpoint_availability.model.entity.AppGrantedAuthority;
import it.unife.sparql_endpoint_availability.model.management.AppUserManagement;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerify extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    private final AppUserManagement appUserManagement;

    public JwtTokenVerify(JwtConfig jwtConfig, SecretKey secretKey, AppUserManagement appUserManagement) {
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.appUserManagement = appUserManagement;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException  {

        String authHeader = request.getHeader("Authorization");
        if(Strings.isNullOrEmpty(authHeader) || !authHeader.startsWith(jwtConfig.getPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(jwtConfig.getPrefix().length());
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder().
                    setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();

            String username = body.getSubject();

            //verify if the user is still present in the database
            if(!appUserManagement.exists(username) || !appUserManagement.isUserEnabled(username)) {
                filterChain.doFilter(request, response);
                return;
            }


            List<Map<String,String>> authorities = (List<Map<String,String>>) body.get("authorities");

            Set<AppGrantedAuthority> appGrantedAuthorities = authorities.stream()
                    .map(m -> new AppGrantedAuthority(m.get("authority")))
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    appGrantedAuthorities);

            logger.info("Authentication successful for user: " + username + " with authorities: " + appGrantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (JwtException e){
            String message = "JWT token is invalid";
            logger.error(message);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            byte[] body = new ObjectMapper()
                    .writeValueAsBytes(Collections.singletonMap("error", message));
            response.getOutputStream().write(body);
            return;
        }

        filterChain.doFilter(request, response);


    }
}
