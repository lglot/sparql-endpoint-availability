package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SparqlEndpointRepository extends CrudRepository<SparqlEndpoint,Long> {
    // This will be AUTO IMPLEMENTED by Spring into a Bean called sparqlEndpointRepository
    // CRUD refers Create, Read, Update, Delete

    boolean existsSparqlEndpointByServiceURL(String serviceURL);

    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql FROM SparqlEndpoint sparql LEFT JOIN FETCH sparql.sparqlEndpointStatuses as status where status.queryDate in (" +
            "SELECT max(s.queryDate) FROM SparqlEndpointStatus s group by s.sparqlEndpoint) order by sparql.id")
    List<SparqlEndpoint> findAllWithLastStatus();

    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql FROM SparqlEndpoint sparql LEFT JOIN FETCH sparql.sparqlEndpointStatuses as status where status.queryDate > ?1 order by sparql.id asc,status.queryDate desc")
    List<SparqlEndpoint> findAllAfterQueryDateStatus(Date queryDate);

    @Query("SELECT s from SparqlEndpoint s order by s.id")
    List<SparqlEndpoint.OnlyURL> findAllURL();

}
