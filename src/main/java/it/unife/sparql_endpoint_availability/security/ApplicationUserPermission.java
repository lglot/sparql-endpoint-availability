package it.unife.sparql_endpoint_availability.security;

public enum ApplicationUserPermission {
    SPARQL_ENDPOINT_READ("sparql_endpoint:read"),
    SPARQL_ENDPOINT_WRITE("sparql_endpoint:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
