package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import org.springframework.data.repository.CrudRepository;

public interface SparqlEndpointRepository extends CrudRepository<SparqlEndpoint,Long> {
    // This will be AUTO IMPLEMENTED by Spring into a Bean called sparqlEndpointRepository
    // CRUD refers Create, Read, Update, Delete

    boolean existsSparqlEndpointByServiceURL(String serviceURL);
}
