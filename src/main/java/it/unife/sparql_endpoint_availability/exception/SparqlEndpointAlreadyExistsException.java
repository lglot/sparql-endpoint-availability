package it.unife.sparql_endpoint_availability.exception;

public class SparqlEndpointAlreadyExistsException extends Exception {

    public SparqlEndpointAlreadyExistsException() {
        super();
    }
    public SparqlEndpointAlreadyExistsException(String url) {
        super("SparqlEndpoint with url " + url + " already exists");
    }

}
