package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Getter
public class UserDto implements Serializable {
    private String username;
    private String role;
    private String jwtToken;
    private boolean enabled;

    public static UserDto fromAppUser(AppUser appUser) {

        return UserDto.builder()
                .username(appUser.getUsername())
                .role(appUser.getAuthorities().stream()
                        .filter(a -> a.getAuthority().startsWith("ROLE_"))
                        .map(a -> a.getAuthority().substring(5))
                        .findFirst()
                        .orElse("USER"))
                .enabled(appUser.isEnabled())
                .build();
    }

    public static List<UserDto> fromAppUsers(List<AppUser> appUsers) {
        return appUsers.stream()
                .map(UserDto::fromAppUser)
                .collect(Collectors.toList());
    }
}