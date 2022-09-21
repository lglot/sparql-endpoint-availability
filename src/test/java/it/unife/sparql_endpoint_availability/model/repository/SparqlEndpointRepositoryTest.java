package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparqlEndpointRepositoryTest {

    @Autowired
    private SparqlEndpointRepository sparqlEndpointRepository;

    private long id_test;

    @Test
    //@Rollback(false)
    void should_save_sparql_endpoint() {
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("url").name("name").build();
        sparqlEndpointRepository.save(sparqlEndpoint);
        assertTrue(sparqlEndpoint.getId() > 0);

        Optional<SparqlEndpoint> sparqlEndpointOptional = sparqlEndpointRepository.findById(sparqlEndpoint.getId());
        assertTrue(sparqlEndpointOptional.isPresent());
        assertEquals(sparqlEndpoint, sparqlEndpointOptional.get());
    }

    @BeforeEach
    @Rollback(false)
    void saveSparqlEndpoints() {
        Date now = new Date();
        Date oneWeekAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);

        for(int i = 1; i <= 5; i++) {
            SparqlEndpoint se = SparqlEndpoint.builder()
                    .url("url" + i)
                    .name("name" + i)
                    .build();
            SparqlEndpointStatus status = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(now)
                    .active(i%2==0)
                    .build();
            SparqlEndpointStatus statusOld = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(oneWeekAgo)
                    .active(i%2==0)
                    .build();
            se.setSparqlEndpointStatuses(Arrays.asList(status, statusOld));
            sparqlEndpointRepository.save(se);
            if(i == 1) {
                id_test = se.getId();
            }
        }
    }


    @Test
    void findAllWithCurrentStatus() {
        List<SparqlEndpoint> sparqlEndpoints = sparqlEndpointRepository.findAllWithCurrentStatus();
        assertEquals(5, sparqlEndpoints.size());
        for(SparqlEndpoint endpoint : sparqlEndpoints) {
            assertNotNull(endpoint.getId());
            assertNotNull(endpoint.getSparqlEndpointStatuses().get(0).getId());
            assertNotNull(endpoint.getSparqlEndpointStatuses().get(1).getId());
        }
    }

    @Test
    void findByIdWithCurrentStatus() {
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByIdWithCurrentStatus(id_test));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals(id_test,sparqlEndpoint.getId());
        assertNotNull(sparqlEndpoint.getSparqlEndpointStatuses().get(0).getId());
        assertNotNull(sparqlEndpoint.getSparqlEndpointStatuses().get(1).getId());
    }

    @Test
    void findAllAfterQueryDateStatus() {
        Date sixDayAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6);
        List<SparqlEndpoint> sparqlEndpoints = sparqlEndpointRepository.findAllAfterQueryDateStatus(sixDayAgo);
        assertEquals(5, sparqlEndpoints.size());
        for(SparqlEndpoint endpoint : sparqlEndpoints) {
            assertTrue(endpoint.getSparqlEndpointStatuses().get(0).getQueryDate().after(sixDayAgo));
        }
    }

    @Test
    void findByIdAfterQueryDateStatus() {
        Date sixDayAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6);
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByIdAfterQueryDateStatus(sixDayAgo, id_test));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals(id_test,sparqlEndpoint.getId());
        assertTrue(sparqlEndpoint.getSparqlEndpointStatuses().get(0).getQueryDate().after(sixDayAgo));
    }

    @Test
    void findOnlyCurrentlyActive() {
        List<SparqlEndpoint> sparqlEndpoints = sparqlEndpointRepository.findOnlyCurrentlyActive();
        assertEquals(2, sparqlEndpoints.size());
        for(SparqlEndpoint endpoint : sparqlEndpoints) {
            assertTrue(endpoint.getSparqlEndpointStatuses().get(0).isActive());
        }
    }

    @Test
    void findByUrlwithCurrentStatus() {
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByUrlWithCurrentStatus("url1"));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals("url1",sparqlEndpoint.getUrl());
        assertNotNull(sparqlEndpoint.getSparqlEndpointStatuses().get(0).getId());
        assertNotNull(sparqlEndpoint.getSparqlEndpointStatuses().get(1).getId());
    }

    @Test
    void findAfterLastWeekStatus_should_return_168_max() {
        Date lastWeek = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("test_week").name("test_week")
                .sparqlEndpointStatuses(new ArrayList<>())
                .build();
        for(int i = 0; i < 200; i++) {
            Date date = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * i);
            SparqlEndpointStatus status = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(sparqlEndpoint)
                    .queryDate(date)
                    .active(i%2==0)
                    .build();
            sparqlEndpoint.getSparqlEndpointStatuses().add(status);
        }
        sparqlEndpointRepository.save(sparqlEndpoint);
        assertTrue(sparqlEndpoint.getId() > 0);
        List<SparqlEndpointStatus> sparqlEndpointStatuses = sparqlEndpointRepository.findByIdAfterQueryDateStatus(lastWeek, sparqlEndpoint.getId()).getSparqlEndpointStatuses();
        assertEquals(168, sparqlEndpointStatuses.size());
    }






}