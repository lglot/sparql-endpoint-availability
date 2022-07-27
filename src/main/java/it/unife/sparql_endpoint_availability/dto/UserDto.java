package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import lombok.*;
import org.checkerframework.checker.signature.qual.ClassGetSimpleName;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Getter
public class UserDto implements Serializable {
    private String username;
    private String role;
    private String jwtToken;

    public static UserDto fromAppUser(AppUser appUser) {
        return UserDto.builder()
                .username(appUser.getUsername())
                .role(appUser.getAuthorities().stream()
                        .filter(a -> a.getAuthority().startsWith("ROLE_"))
                        .map(a -> a.getAuthority().substring(5))
                        .findFirst()
                        .orElse("USER"))
                .build();
    }


}
