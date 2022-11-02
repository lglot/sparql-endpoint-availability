package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparqlEndpointRepositoryTest {

    @Autowired
    private SparqlEndpointRepository sparqlEndpointRepository;

    LocalDateTime now;
    LocalDateTime twoDaysAgo;
    LocalDateTime oneWeekAgo;

    @BeforeAll
    public void init() {
       this.now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
       this.twoDaysAgo = now.minusDays(2);
       this.oneWeekAgo = now.minusWeeks(1);
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
            assertTrue(endpoint.getSparqlEndpointStatuses().get(0).getQueryDate().equals(this.now));
        }
    }

    @Test
    void findByUrlwithCurrentStatus() {
        Optional<SparqlEndpoint> sparqlEndpointOptional = Optional.ofNullable(sparqlEndpointRepository.findByUrlWithCurrentStatus("url1"));
        assertTrue(sparqlEndpointOptional.isPresent());
        SparqlEndpoint sparqlEndpoint = sparqlEndpointOptional.get();
        assertEquals("url1", sparqlEndpoint.getUrl());
        assertEquals(sparqlEndpoint.getSparqlEndpointStatuses().size(), 1);
        assertTrue(sparqlEndpoint.getSparqlEndpointStatuses().get(0).getQueryDate().equals(this.now));
    }

    @Test
    void findAllAfterQueryDateStatus() {
        LocalDateTime sixDayAgo = now.minusDays(6);
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

        LocalDateTime sixDayAgo = now.minusDays(6);

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
    void findAfterLastWeekStatus_shouldNotReturnOlderStatus() {
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        SparqlEndpoint sparqlEndpoint = SparqlEndpoint.builder().url("test_week").name("test_week")
                .sparqlEndpointStatuses(new ArrayList<>())
                .build();
        for (int i = 0; i < 200; i++) {
            LocalDateTime date = now.minusHours(i);
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


        List<SparqlEndpointStatus> statusesTruncated = sparqlEndpointRepository.findByUrlAfterQueryDateStatus(oneWeekAgo, "test_week").getSparqlEndpointStatuses();
        SparqlEndpointStatus oldestStatus = statusesTruncated
                .stream()
                .min(Comparator.comparing(SparqlEndpointStatus::getQueryDate))
                .get();

        assertTrue(oldestStatus.getQueryDate().isAfter(oneWeekAgo));
    }

}