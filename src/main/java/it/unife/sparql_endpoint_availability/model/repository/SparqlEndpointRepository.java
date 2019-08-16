package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;


public interface SparqlEndpointRepository extends CrudRepository<SparqlEndpoint,Long> {
    // This will be AUTO IMPLEMENTED by Spring into a Bean called sparqlEndpointRepository
    // CRUD refers Create, Read, Update, Delete


    /*@Query("SELECT new SparqlEndpoint(sparql, MAX(status.query_date))" +
            " from sparql_endpoint as sparql" +
            " join sparql_endpoint_status as status" +
            " on sparql.id=status.sparql_endpoint_id" +
            " group by sparql")
    List<SparqlEndpoint> getAllwithLastUpdate();*/

    boolean existsSparqlEndpointByServiceURL(String serviceURL);
}
