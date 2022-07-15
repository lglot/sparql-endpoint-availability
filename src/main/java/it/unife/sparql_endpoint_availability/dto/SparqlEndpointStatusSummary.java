package it.unife.sparql_endpoint_availability.dto;

import lombok.*;

//Classe DTO, serve per trasferire informazioni degli sparrql enpoint al client
@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointStatusSummary {

    private String url;

    /*
     * this variable indicates whether the sparql is active,
     * or is inactive for less or more than a week
     */
    private String status;

    private String name;

    private double uptimeLast24h;
    private double uptimelast7d;
}
