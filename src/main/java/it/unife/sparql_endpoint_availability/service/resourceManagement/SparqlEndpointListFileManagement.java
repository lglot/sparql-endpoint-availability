package it.unife.sparql_endpoint_availability.service.resourceManagement;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import java.util.List;
import java.util.Set;

public interface SparqlEndpointListFileManagement {

//    List<String> listSPARQLEndpoints();
    Set<SparqlEndpoint> getSparqlEndpoints();

    /*verifica se il file è stato modificato dall'ultimo accesso*/
    boolean isModified();
}
