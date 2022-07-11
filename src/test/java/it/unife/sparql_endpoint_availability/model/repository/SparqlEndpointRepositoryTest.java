package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SparqlEndpointRepositoryTest {

    @Autowired
    private SparqlEndpointRepository sparqlEndpointRepository;
    @Autowired
    private SparqlEndpointStatusRepository sparqlEndpointStatusRepository;
    @Test
    //@Rollback(false)
    void should_save_sparql_endpoint() {
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("url1").name("name1").build();
        sparqlEndpointRepository.save(sparqlEndpoint);
        assertTrue(sparqlEndpoint.getId() > 0);
        SparqlEndpointStatus sparqlEndpointStatus = SparqlEndpointStatus.builder()
                .sparqlEndpoint(sparqlEndpoint)
                .queryDate(new Date())
                .active(true)
                .build();
        sparqlEndpointStatusRepository.save(sparqlEndpointStatus);
        assertTrue(sparqlEndpointStatus.getId() > 0);

        Optional<SparqlEndpoint> se2 = sparqlEndpointRepository.findById(sparqlEndpoint.getId());
        assertTrue(se2.isPresent());
        System.out.println(se2.get().getSparqlEndpointStatuses());
        System.out.println(sparqlEndpointStatus);
    }

}