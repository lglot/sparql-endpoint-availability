package it.unife.sparql_endpoint_availability.exception;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String username) {
        super(String.format("User %s already exists", username));
    }
}
