package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.SparqlEndpoint;
import org.springframework.data.repository.CrudRepository;

public interface SparqlEndpointRepository extends CrudRepository<SparqlEndpoint,Long> {
    // This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
    // CRUD refers Create, Read, Update, Delete
}
