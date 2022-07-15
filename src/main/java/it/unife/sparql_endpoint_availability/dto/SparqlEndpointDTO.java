package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointDTO {

    private String url;
    private String name;
    private String status;

    public static String getStatusString(SparqlEndpointStatus status) {
        if (status.isActive()) {
            return "active";
        } else {
            return "inactive";
        }
    }

    public static List<SparqlEndpointDTO> fromSparqlEndpointList(List<SparqlEndpoint> sparqlEndpointList) {
        return sparqlEndpointList.stream().map(SparqlEndpointDTO::fromSparqlEndpoint).collect(Collectors.toList());
    }

    public static SparqlEndpointDTO fromSparqlEndpoint(SparqlEndpoint sparqlEndpoint) {
        return SparqlEndpointDTO.builder()
                .url(sparqlEndpoint.getUrl())
                .name(sparqlEndpoint.getName())
                .status(getStatusString(sparqlEndpoint.getSparqlEndpointStatuses().get(0)))
                .build();
    }
}
