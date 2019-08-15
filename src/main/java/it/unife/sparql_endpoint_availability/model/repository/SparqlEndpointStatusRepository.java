package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SparqlEndpointStatusRepository extends CrudRepository<SparqlEndpointStatus,Long> {


}
