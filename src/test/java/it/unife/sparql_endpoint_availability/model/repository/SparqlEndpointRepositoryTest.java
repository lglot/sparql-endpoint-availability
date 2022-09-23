package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparqlEndpointRepositoryTest {

    @Autowired
    private SparqlEndpointRepository sparqlEndpointRepository;
    Date now;
    Date twoDaysAgo;
    Date oneWeekAgo;

    @BeforeAll
    public void init() {
       this.now = new Date();
       this.twoDaysAgo = new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000);
       this.oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    }


    @BeforeEach
    void saveSparqlEndpoints() {
        for (int i = 1; i <= 5; i++) {
            SparqlEndpoint se = SparqlEndpoint.builder()
                    .url("url" + i)
                    .name("name" + i)
                    .build();
            SparqlEndpointStatus status = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(this.now)
                    .active(i % 2 == 0)
                    .build();
            SparqlEndpointStatus twoDaysAgo = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(this.twoDaysAgo)
                    .active(i % 2 == 0)
                    .build();
            SparqlEndpointStatus statusOld = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(se)
                    .queryDate(this.oneWeekAgo)
                    .active(i % 2 == 0)
                    .build();
            se.setSparqlEndpointStatuses(Arrays.asList(status, twoDaysAgo,statusOld));
            sparqlEndpointRepository.save(se);
        }
    }

    @AfterEach
    void deleteSparqlEndpoints() {
        sparqlEndpointRepository.deleteAll();
    }

    @Test
    @Rollback(true)
    void should_save_sparql_endpoint() {
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("url").name("name").build();
        sparqlEndpointRepository.save(sparqlEndpoint);
        assertTrue(sparqlEndpoint.getId() > 0);

        Optional<SparqlEndpoint> sparqlEndpointOptional = sparqlEndpointRepository.findById(sparqlEndpoint.getId());
        assertTrue(sparqlEndpointOptional.isPresent());
        assertEquals(sparqlEndpoint, sparqlEndpointOptional.get());
    }


    // deve ritornare tutti gli endpoint con un solo stato e che sia il più recente
    @Test
    void findAllWithCurrentStatus_should_return_one_status_per_endpoint_with_the_most_recent_query_date() {
        List<SparqlEndpoint> seWithCurrentStatus = sparqlEndpointRepository.findAllWithCurrentStatus();
        assertEquals(5, seWithCurrentStatus.size());
        for (SparqlEndpoint endpoint : seWithCurrentStatus) {
            assertNotNull(endpoint.getId());
            assertEquals(1, endpoint.getSparqlEndpointStatuses().size());
            assertTrue(this.now.equals(endpoint.getSparqlEndpointStatuses().get(0).getQueryDate()));
        }
    }

    @Test
    void findByUrlwithCurrentStatus() {
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByUrlWithCurrentStatus("url1"));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals("url1", sparqlEndpoint.getUrl());
        assertEquals(sparqlEndpoint.getSparqlEndpointStatuses().size(), 1);
        assertTrue(this.now.equals(sparqlEndpoint.getSparqlEndpointStatuses().get(0).getQueryDate()));
    }

    @Test
    void findAllAfterQueryDateStatus() {
        Date sixDayAgo = new Date(now.getTime() - 6 * 24 * 60 * 60 * 1000);
        List<SparqlEndpoint> sparqlEndpoints = sparqlEndpointRepository.findAllAfterQueryDateStatus(sixDayAgo);
        assertEquals(5, sparqlEndpoints.size());
        for (SparqlEndpoint endpoint : sparqlEndpoints) {
            assertNotNull(endpoint.getId());
            assertEquals(2, endpoint.getSparqlEndpointStatuses().size());
            assertTrue(endpoint.getSparqlEndpointStatuses().stream().anyMatch(s -> this.now.equals(s.getQueryDate())));
            assertTrue(endpoint.getSparqlEndpointStatuses().stream().anyMatch(s -> this.twoDaysAgo.equals(s.getQueryDate())));
        }
    }

    @Test
    void findByUrlAfterQueryDateStatus() {
        Date sixDayAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 6);
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByUrlAfterQueryDateStatus(sixDayAgo, "url1"));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals("url1", sparqlEndpoint.getUrl());
        assertEquals(2, sparqlEndpoint.getSparqlEndpointStatuses().size());
        assertTrue(sparqlEndpoint.getSparqlEndpointStatuses().stream().anyMatch(s -> this.now.equals(s.getQueryDate())));
        assertTrue(sparqlEndpoint.getSparqlEndpointStatuses().stream().anyMatch(s -> this.twoDaysAgo.equals(s.getQueryDate())));
    }

    @Test
    void findOnlyCurrentlyActive() {
        List<SparqlEndpoint> sparqlEndpoints = sparqlEndpointRepository.findOnlyCurrentlyActive();
        assertEquals(2, sparqlEndpoints.size());
        for (SparqlEndpoint endpoint : sparqlEndpoints) {
            assertTrue(endpoint.getSparqlEndpointStatuses().get(0).isActive());
        }
    }

    @Test
    @Rollback(true)
    void findAfterLastWeekStatus_shouldNotReturnOlderStatus() {
        Date aWeekAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7);
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("test_week").name("test_week")
                .sparqlEndpointStatuses(new ArrayList<>())
                .build();
        for (int i = 0; i < 200; i++) {
            Date date = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * i);
            SparqlEndpointStatus status = SparqlEndpointStatus.builder()
                    .sparqlEndpoint(sparqlEndpoint)
                    .queryDate(date)
                    .active(i % 2 == 0)
                    .build();
            sparqlEndpoint.getSparqlEndpointStatuses().add(status);
        }
        sparqlEndpointRepository.save(sparqlEndpoint);
        long id = sparqlEndpoint.getId();
        assertTrue(id > 0);


        List<SparqlEndpointStatus> statusesTruncated = sparqlEndpointRepository.findByUrlAfterQueryDateStatus(aWeekAgo, "test_week").getSparqlEndpointStatuses();
        SparqlEndpointStatus oldestStatus = statusesTruncated
                .stream()
                .min(Comparator.comparing(SparqlEndpointStatus::getQueryDate))
                .get();

        assertTrue(oldestStatus.getQueryDate().after(aWeekAgo));
    }

}