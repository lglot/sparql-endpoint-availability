package it.unife.sparql_endpoint_availability.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String role) {
        super("Role " + role + " not found");
    }

    public RoleNotFoundException() {
        super();
    }
}
