package it.unife.sparql_endpoint_availability.service.resourceManagement;

import java.util.List;

public interface SparqlListResource {

    List<String> read();

    boolean isModified();
}
