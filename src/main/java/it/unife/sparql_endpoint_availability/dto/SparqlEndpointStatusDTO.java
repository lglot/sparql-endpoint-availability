package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import lombok.*;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import static it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatusLabel.*;
import static java.lang.Math.round;

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



    public static SparqlEndpointStatusDTO fromSparqlEndpointStatusList(List<SparqlEndpointStatus> sparqlEndpointStatuses, boolean firstIsNewest) {

        SparqlEndpointStatusDTO sparqlEndpointStatusDTO = new SparqlEndpointStatusDTO();

        int newestStatusIndex = firstIsNewest ? 0 : sparqlEndpointStatuses.size() - 1;
        int oldestStatusIndex = firstIsNewest ? sparqlEndpointStatuses.size() - 1 : 0;

        SparqlEndpointStatus newestStatus = sparqlEndpointStatuses.get(newestStatusIndex);
        SparqlEndpointStatus oldestStatus = sparqlEndpointStatuses.get(oldestStatusIndex);

        sparqlEndpointStatusDTO.setUrl(newestStatus.getSparqlEndpoint().getUrl());
        sparqlEndpointStatusDTO.setName(newestStatus.getSparqlEndpoint().getName());


        boolean labelFound = false;
        if(newestStatus.isActive()) {
            sparqlEndpointStatusDTO.setStatus(ACTIVE.getLabel());
            labelFound = true;
        }
        //se non è passata ancora un giorno, lo stato è generalmente inattivo
        else if(sparqlEndpointStatuses.size() < 24) {
            sparqlEndpointStatusDTO.setStatus(GENERAL_INACTIVE.getLabel());
            labelFound = true;
        }



        double uptimeLast24h = 0;
        double uptimeLast7d = 0;

        for (SparqlEndpointStatus status : sparqlEndpointStatuses) {
            if (status.isActive()) {
                uptimeLast7d += 1;

                if (status.getQueryDate().getTime() > DateUtils.addDays(new Date(), -1).getTime()) {
                    uptimeLast24h += 1;
                    if(!labelFound || sparqlEndpointStatusDTO.getStatus().equals(INACTIVE_MOREDAY.getLabel())) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_LESSDAY.getLabel());
                        labelFound = true;
                    }
                }
                else {
                    if (!labelFound) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREDAY.getLabel());
                        labelFound = true;
                    }
                }
            }
        }
        if(!labelFound) {
            sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREWEEK.getLabel());
        }

        //se non è passato almeno un giorno l'uptime delle ultime 24 ore viene settato a -1
        if(sparqlEndpointStatuses.size() < 24) {
            sparqlEndpointStatusDTO.setUptimeLast24h(-1);
        }
        else {
            BigDecimal uptimeLast24hBD = new BigDecimal(uptimeLast24h);
            BigDecimal d = new BigDecimal(24);
            BigDecimal uptimeLast24hBDdivided = uptimeLast24hBD.divide(d, 2, RoundingMode.HALF_UP);
            sparqlEndpointStatusDTO.setUptimeLast24h(uptimeLast24hBDdivided.doubleValue());
        }
        //se non è passato almeno una settimana l'uptime delle ultime 7 giorni viene settato a -1
        if(sparqlEndpointStatuses.size() < 168) {
            sparqlEndpointStatusDTO.setUptimelast7d(-1);
        }
        else {
            BigDecimal uptimeLast7dBD = new BigDecimal(uptimeLast7d);
            BigDecimal w = new BigDecimal(168);
            BigDecimal uptimeLast7dBDdivided = uptimeLast7dBD.divide(w, 2, RoundingMode.HALF_UP);
            sparqlEndpointStatusDTO.setUptimelast7d(uptimeLast7dBDdivided.doubleValue());
        }

        return sparqlEndpointStatusDTO;
    }
}
