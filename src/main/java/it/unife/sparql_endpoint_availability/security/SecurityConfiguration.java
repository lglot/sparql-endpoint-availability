package it.unife.sparql_endpoint_availability.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    private final String BASE_URL = "/api/v1/";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/","index","/css/**","/js/**").permitAll()
                //.antMatchers(HttpMethod.DELETE, "/
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .headers().frameOptions().disable();
        return http.build();
    }



    //user detail service
    @Bean(name = "userDetailsService")
    public UserDetailsService userDetailsService() {
        UserDetails luigi = User.builder()
                .username("luigi")
                .password(passwordEncoder.encode("luigi"))
                .roles(ADMIN.name())
                .build();

        UserDetails mario = User.builder()
                .username("mario")
                .password(passwordEncoder.encode("mario"))
                .roles(USER.name())
                .build();

        return new InMemoryUserDetailsManager(luigi, mario);
    }

}