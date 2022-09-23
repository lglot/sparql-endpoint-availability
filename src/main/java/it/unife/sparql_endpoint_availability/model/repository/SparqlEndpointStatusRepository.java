package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SparqlEndpointStatusRepository extends JpaRepository<SparqlEndpointStatus,Long> {

    @Query("SELECT DISTINCT s " +
            "from SparqlEndpointStatus s " +
            "where s.queryDate IN (" +
            "   select max(s.queryDate) " +
            "   from SparqlEndpointStatus s " +
            "   group by s.sparqlEndpoint)")
    List<SparqlEndpointStatus> findLastSparqlEnpointStatus();

    List<SparqlEndpointStatus> findSparqlEndpointStatusByQueryDateAfter(LocalDateTime queryDate);

    SparqlEndpointStatus findTopByOrderByQueryDate();
    
    void deleteSparqlEndpointStatusByQueryDateBefore(LocalDateTime queryDate);
}
