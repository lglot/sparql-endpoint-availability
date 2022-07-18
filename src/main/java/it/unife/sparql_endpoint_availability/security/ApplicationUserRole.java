package it.unife.sparql_endpoint_availability.security;

import com.google.common.collect.Sets;

import java.util.Set;

import static it.unife.sparql_endpoint_availability.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    USER(Sets.newHashSet(SPARQL_ENDPOINT_READ)),
    ADMIN(Sets.newHashSet(SPARQL_ENDPOINT_READ, SPARQL_ENDPOINT_WRITE));

    private final Set<ApplicationUserPermission> permissions;


    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
