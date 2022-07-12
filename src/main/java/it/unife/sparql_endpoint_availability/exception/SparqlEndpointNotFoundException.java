package it.unife.sparql_endpoint_availability.exception;

import javax.validation.constraints.NotNull;

public class SparqlEndpointNotFoundException extends Exception {

    public SparqlEndpointNotFoundException() {
        super();
    }
    public SparqlEndpointNotFoundException(@NotNull Long id) {
        super("SparqlEndpoint with id " + id + " not found");
    }
}
