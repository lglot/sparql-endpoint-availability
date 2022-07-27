package it.unife.sparql_endpoint_availability.security;

import com.google.common.collect.Sets;
import it.unife.sparql_endpoint_availability.model.entity.AppGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    USER(Sets.newHashSet(SPARQL_ENDPOINT_READ)),
    ADMIN(Sets.newHashSet(SPARQL_ENDPOINT_READ, SPARQL_ENDPOINT_WRITE, USER_READ, USER_WRITE));

    private final Set<ApplicationUserPermission> permissions;


    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }

    public Set<AppGrantedAuthority> getGrantedAuthorities() {
        Set<AppGrantedAuthority> permissions= getPermissions().stream()
                .map(p -> new AppGrantedAuthority(p.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new AppGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
