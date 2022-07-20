package it.unife.sparql_endpoint_availability.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.ADMIN;
import static it.unife.sparql_endpoint_availability.security.ApplicationUserRole.USER;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration  {

    PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfiguration(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .loginPage("/login").permitAll()
                    .loginProcessingUrl("/login?lang={lang}")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=true")
                .and()
                .rememberMe().tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login?logout");
//                .and()
//                .headers().frameOptions().disable();
        return http.build();
    }



    //user detail service
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails luigi = User.builder()
                .username("luigi")
                .password(passwordEncoder.encode("luigi"))
//                .roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        UserDetails mario = User.builder()
                .username("mario")
                .password(passwordEncoder.encode("mario"))
//                .roles(USER.name())
                .authorities(USER.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(luigi, mario);
    }

}
