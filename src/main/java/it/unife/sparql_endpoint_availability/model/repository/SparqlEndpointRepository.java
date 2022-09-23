package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface SparqlEndpointRepository extends JpaRepository<SparqlEndpoint, Long> {

    SparqlEndpoint findByUrl(String url);
    List<SparqlEndpoint> findAllByOrderById();
    boolean existsByUrl(String url);
    void deleteByUrl(String url);


    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql " +
            "FROM SparqlEndpoint sparql " +
            "LEFT JOIN sparql.sparqlEndpointStatuses as status " +
            "where (status.id is NULL OR status.queryDate in (" +
                "SELECT max(s.queryDate) " +
                "FROM SparqlEndpointStatus s " +
                "group by s.sparqlEndpoint)) " +
            "order by sparql.id")
    List<SparqlEndpoint> findAllWithCurrentStatus();

    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql " +
            "FROM SparqlEndpoint sparql " +
            "LEFT JOIN sparql.sparqlEndpointStatuses as status " +
            "where (status.id is NULL OR status.queryDate in (" +
                "SELECT max(s.queryDate) " +
                "FROM SparqlEndpointStatus s " +
                "group by s.sparqlEndpoint)) " +
            "AND sparql.url=?1 " +
            "order by sparql.id")
    SparqlEndpoint findByUrlWithCurrentStatus(@Param("url") String url);

    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql " +
            "FROM SparqlEndpoint sparql " +
            "LEFT JOIN FETCH sparql.sparqlEndpointStatuses as status " +
            "where status.id is NULL OR status.queryDate > ?1 " +
            "order by sparql.id asc,status.queryDate desc")
    List<SparqlEndpoint> findAllAfterQueryDateStatus(Date queryDate);

    @EntityGraph(value = "SparqlEndpoint.detail", type = EntityGraph.EntityGraphType.FETCH)
    @Query(" SELECT sparql " +
            "FROM SparqlEndpoint sparql " +
            "LEFT JOIN FETCH sparql.sparqlEndpointStatuses as status" +
            " where status.id is NULL OR status.queryDate > ?1 " +
            "and sparql.url=?2 " +
            "order by sparql.id asc,status.queryDate desc")
    SparqlEndpoint findByUrlAfterQueryDateStatus(Date queryDate,String url);


    @Query("SELECT s " +
            "from SparqlEndpoint s " +
            "JOIN FETCH s.sparqlEndpointStatuses as st " +
            "where st.queryDate in (" +
                " SELECT max(s.queryDate) " +
                " FROM SparqlEndpointStatus s " +
                " group by s.sparqlEndpoint) " +
            "AND st.active=true " 
//           + "order by s.id"
    )
    List<SparqlEndpoint> findOnlyCurrentlyActive();



}
