package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusLabel.*;

//Classe DTO, serve per trasferire informazioni degli sparrql enpoint al client
@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointStatusDTO {

    private String url;

    /*
     * this variable indicates whether the sparql is active,
     * or is inactive for less or more than a week
     */
    private String status;

    private String name;

    private double uptimeLast24h;
    private double uptimelast7d;

    public static SparqlEndpointStatusDTO fromSparqlEndpointStatusList(
            List<SparqlEndpointStatus> sparqlEndpointStatuses, boolean firstIsNewest) {

        SparqlEndpointStatusDTO sparqlEndpointStatusDTO = new SparqlEndpointStatusDTO();

        int newestStatusIndex = firstIsNewest ? 0 : sparqlEndpointStatuses.size() - 1;
        int oldestStatusIndex = firstIsNewest ? sparqlEndpointStatuses.size() - 1 : 0;

        SparqlEndpointStatus newestStatus = sparqlEndpointStatuses.get(newestStatusIndex);
        SparqlEndpointStatus oldestStatus = sparqlEndpointStatuses.get(oldestStatusIndex);

        sparqlEndpointStatusDTO.setUrl(newestStatus.getSparqlEndpoint().getUrl());
        sparqlEndpointStatusDTO.setName(newestStatus.getSparqlEndpoint().getName());

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);

        boolean labelFound = false;
        if (newestStatus.isActive()) {
            sparqlEndpointStatusDTO.setStatus(ACTIVE.getLabel());
            labelFound = true;
        }
        // se non è passata ancora un giorno, lo stato è generalmente inattivo
        else if (oldestStatus.getQueryDate().isAfter(yesterday)) {
            sparqlEndpointStatusDTO.setStatus(GENERAL_INACTIVE.getLabel());
            labelFound = true;
        }

        double uptimeLast24h = 0;
        double uptimeLast7d = 0;

        for (SparqlEndpointStatus status : sparqlEndpointStatuses) {
            if (status.getQueryDate().isAfter(lastWeek) && status.isActive()) {
                uptimeLast7d += 1;

                if (status.getQueryDate().isAfter(yesterday)) {
                    uptimeLast24h += 1;
                    if (!labelFound || sparqlEndpointStatusDTO.getStatus().equals(INACTIVE_MOREDAY.getLabel())) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_LESSDAY.getLabel());
                        labelFound = true;
                    }
                } else {
                    if (!labelFound) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREDAY.getLabel());
                        labelFound = true;
                    }
                }
            }
        }
        if (!labelFound) {
            if (oldestStatus.getQueryDate().isBefore(lastWeek)) {
                sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREWEEK.getLabel());
            } else if (oldestStatus.getQueryDate().isBefore(yesterday)) {
                sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREDAY.getLabel());
            } else
                sparqlEndpointStatusDTO.setStatus(INACTIVE_LESSDAY.getLabel());
        }

        // se non è passato almeno un giorno l'uptime delle ultime 24 ore viene settato
        // a -1
        if (oldestStatus.getQueryDate().isAfter(yesterday)) {
            sparqlEndpointStatusDTO.setUptimeLast24h(-1);
        } else {
            sparqlEndpointStatusDTO.setUptimeLast24h(calculateUptime(sparqlEndpointStatuses, yesterday, uptimeLast24h));
        }
        // se non è passato almeno una settimana l'uptime delgli ultimi sette giorni
        // viene settato a -1
        if (oldestStatus.getQueryDate().isAfter(lastWeek.minusHours(12))) {
            sparqlEndpointStatusDTO.setUptimelast7d(-1);
        } else {
            sparqlEndpointStatusDTO.setUptimelast7d(calculateUptime(sparqlEndpointStatuses, lastWeek, uptimeLast7d));
        }

        return sparqlEndpointStatusDTO;
    }

    private static double calculateUptime(List<SparqlEndpointStatus> sparqlEndpointStatuses, LocalDateTime interval,
            double activeCounted) {
        long statusCountAfterInterval = sparqlEndpointStatuses.stream().filter(s -> s.getQueryDate().isAfter(interval))
                .count();
        BigDecimal a = BigDecimal.valueOf(Math.min(activeCounted, statusCountAfterInterval));
        BigDecimal b = new BigDecimal(statusCountAfterInterval);
        BigDecimal uptime = a.divide(b, 2, RoundingMode.HALF_UP);
        return uptime.doubleValue();
    }
}
