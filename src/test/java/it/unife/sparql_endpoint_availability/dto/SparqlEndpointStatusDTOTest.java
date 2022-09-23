package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusLabel.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SparqlEndpointStatusDTOTest {

    List<SparqlEndpointStatus> statuses;
    List<SparqlEndpointStatus> inverted_statuses;
    long hourInAWeek = 24 * 7;
    long hourInADay = 24;
    long countStatusinAWeek;
    long countStatusinADay;

    @BeforeAll
    void setUp() {
        SparqlEndpoint se = SparqlEndpoint.builder().url("http://example.org").name("Example").build();
        statuses = new ArrayList<>();
        inverted_statuses = new ArrayList<>();
        for(int i = 0; i < hourInAWeek+hourInADay; i++) {
            SparqlEndpointStatus s = SparqlEndpointStatus.builder()
                            .sparqlEndpoint(se)
                            .queryDate(LocalDateTime.now().minusHours(i))
                            .build();
            statuses.add(s);
            inverted_statuses.add(0, s);

        }
        countStatusinADay =  statuses.stream().filter(s -> s.getQueryDate().isAfter(LocalDateTime.now().minusDays(1))).count();
        countStatusinAWeek = statuses.stream().filter(s -> s.getQueryDate().isAfter(LocalDateTime.now().minusDays(7))).count();
    }
    //se tutti gli stati sono attivi, allora lo sparql endpoint è attivo
    // e gli uptime sono al 100%
    @Test
    void allActive_shouldReturnACTIVE() {
        statuses.forEach(s -> s.setActive(true));
        inverted_statuses.forEach(s -> s.setActive(true));

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuses, true);
        assertEquals(ACTIVE.getLabel(), dto.getStatus());
        assertEquals(1, dto.getUptimeLast24h());
        assertEquals(1, dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(inverted_statuses, false);
        assertEquals(ACTIVE.getLabel(), dto.getStatus());
        assertEquals(1, dto.getUptimeLast24h());
        assertEquals(1, dto.getUptimelast7d());
    }

    //se tutti gli stati sono inattivi, allora lo sparql endpoint è inattivo da più di una settimana
    // e gli uptime sono allo 0%
    @Test
    void allInactive_shouldReturnINACTIVE_MOREWEEK() {
        statuses.forEach(s -> s.setActive(false));
        inverted_statuses.forEach(s -> s.setActive(false));

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuses, true);
        assertEquals(INACTIVE_MOREWEEK.getLabel(), dto.getStatus());
        assertEquals(0, dto.getUptimeLast24h());
        assertEquals(0, dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(inverted_statuses, false);
        assertEquals(INACTIVE_MOREWEEK.getLabel(), dto.getStatus());
        assertEquals(0, dto.getUptimeLast24h());
        assertEquals(0, dto.getUptimelast7d());
    }

    //se gli stati sono attivi solo nelle ultime 24 ore, allora lo sparql endpoint è attivo
    // e gli uptime sono al 100% nelle ultime 24 ore e a un valore inferiore al 100% negli ultimi 7 giorni
    @Test
    void lastDayActive_shouldReturnACTIVE() {
        for(int i = 0; i < 24; i++) {
            statuses.get(i).setActive(true);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(true);
        }
        for(int i = 24; i < statuses.size(); i++) {
            statuses.get(i).setActive(false);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(false);
        }

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuses, true);
        assertEquals(ACTIVE.getLabel(), dto.getStatus());
        assertEquals(1, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision(24, countStatusinAWeek), dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(inverted_statuses, false);
        assertEquals(ACTIVE.getLabel(), dto.getStatus());
        assertEquals(1, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision(24, countStatusinAWeek), dto.getUptimelast7d());
    }

    //se gli stati sono inattivi solo nelle ultime 24 ore, allora lo sparql endpoint è inattivo da più di un giorno (e meno di una settimana)
    // e gli uptime sono al 0% nelle ultime 24 ore e a un valore superiore al 0% negli ultimi 7 giorni
    @Test
    void lastDayInactive_shouldReturnINACTIVE_MOREDAY() {
        for(int i = 0; i < 24; i++) {
            statuses.get(i).setActive(false);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(false);
        }
        for(int i = 24; i < statuses.size(); i++) {
            statuses.get(i).setActive(true);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(true);
        }

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuses, true);
        assertEquals(INACTIVE_MOREDAY.getLabel(), dto.getStatus());
        assertEquals(0, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision(countStatusinAWeek-24, countStatusinAWeek), dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(inverted_statuses, false);
        assertEquals(INACTIVE_MOREDAY.getLabel(), dto.getStatus());
        assertEquals(0, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision((countStatusinAWeek-24), countStatusinAWeek), dto.getUptimelast7d());
    }

    //se gli stati sono inattivi nelle ultime 12 ore e attivi nelle altre, allora lo sparql endpoint è inattivo da meno di un giorno
    // e l'uptime è al 50% nelle ultime 24 ore
    @Test
    void lastDayPartiallyInactive_shouldReturnINACTIVE_LESSDAY() {
        for(int i = 0; i < 12; i++) {
            statuses.get(i).setActive(false);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(false);
        }
        for(int i = 12; i < statuses.size(); i++) {
            statuses.get(i).setActive(true);
            inverted_statuses.get(inverted_statuses.size() - i - 1).setActive(true);
        }

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuses, true);
        assertEquals(INACTIVE_LESSDAY.getLabel(), dto.getStatus());
        assertEquals(0.5, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision(countStatusinAWeek-12, countStatusinAWeek), dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(inverted_statuses, false);
        assertEquals(INACTIVE_LESSDAY.getLabel(), dto.getStatus());
        assertEquals(0.5, dto.getUptimeLast24h());
        assertEquals(bigDecimalDivision((countStatusinAWeek-12), countStatusinAWeek), dto.getUptimelast7d());
    }

    //se la lista degli stati non contiene almeno i controlli di un giorno intero e lo stato è inattivo, ritorna GENREAL_INACTIVE
    @Test
    void lastDayPartiallyInactiveAndSizeStatusLessThanADay_shoudReturnGENERAL_INACTIVE(){
        List <SparqlEndpointStatus> statuesTruncated = statuses.subList(0, 12);
        List <SparqlEndpointStatus> invertedStatuesTruncated = inverted_statuses.subList(inverted_statuses.size() - 12, inverted_statuses.size());

        for(int i = 0; i < 6; i++) {
            statuesTruncated.get(i).setActive(false);
            invertedStatuesTruncated.get(invertedStatuesTruncated.size() - i - 1).setActive(false);
        }

        for(int i = 6; i < statuesTruncated.size(); i++) {
            statuesTruncated.get(i).setActive(true);
            invertedStatuesTruncated.get(invertedStatuesTruncated.size() - i - 1).setActive(true);
        }

        SparqlEndpointStatusDTO dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(statuesTruncated, true);
        assertEquals(GENERAL_INACTIVE.getLabel(), dto.getStatus());
        assertEquals(-1, dto.getUptimeLast24h());
        assertEquals(-1, dto.getUptimelast7d());

        dto = SparqlEndpointStatusDTO.fromSparqlEndpointStatusList(invertedStatuesTruncated, false);
        assertEquals(GENERAL_INACTIVE.getLabel(), dto.getStatus());
        assertEquals(-1, dto.getUptimeLast24h());
        assertEquals(-1, dto.getUptimelast7d());
    }

    double bigDecimalDivision(double a, double b){
        BigDecimal aBD = new BigDecimal(a);
        BigDecimal bBD = new BigDecimal(b);
        return aBD.divide(bBD, 2, RoundingMode.HALF_UP).doubleValue();
    }
}