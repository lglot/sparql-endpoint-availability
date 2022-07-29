package it.unife.sparql_endpoint_availability.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String username) {
        super("User " + username + " not found");
    }

    public UserNotFoundException(){
        super("User not found");
    }

}
