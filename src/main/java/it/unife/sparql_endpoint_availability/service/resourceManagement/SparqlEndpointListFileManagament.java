package it.unife.sparql_endpoint_availability.service.resourceManagement;

import java.util.List;

public interface SparqlEndpointListFileManagament {

    List<String> read();

    /*verifica se il file è stato modificato dall'ultimo accesso*/
    boolean isModified();
}
