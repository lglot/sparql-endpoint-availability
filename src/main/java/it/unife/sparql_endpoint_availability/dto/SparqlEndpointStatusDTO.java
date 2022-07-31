package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import lombok.*;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
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
        //se non è passata ancora una settimana, lo stato è generalmente inattivo
        else if(oldestStatus.getQueryDate().getTime() > DateUtils.addDays(new Date(), -7).getTime()) {
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
                    if(!labelFound || sparqlEndpointStatusDTO.getStatus().equals(INACTIVE_LESSWEEK.getLabel())) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_LESSDAY.getLabel());
                        labelFound = true;
                    }
                }
                else {
                    if (!labelFound) {
                        sparqlEndpointStatusDTO.setStatus(INACTIVE_LESSWEEK.getLabel());
                        labelFound = true;
                    }
                }
            }
        }
        if(!labelFound) {
            sparqlEndpointStatusDTO.setStatus(INACTIVE_MOREWEEK.getLabel());
        }
        sparqlEndpointStatusDTO.setUptimeLast24h(uptimeLast24h/(Math.min(sparqlEndpointStatuses.size(), 24)));
        sparqlEndpointStatusDTO.setUptimelast7d(uptimeLast7d/sparqlEndpointStatuses.size());

        return sparqlEndpointStatusDTO;
    }
}
