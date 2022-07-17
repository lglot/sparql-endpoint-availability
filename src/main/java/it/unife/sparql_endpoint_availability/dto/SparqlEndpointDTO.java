package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpointStatus;
import lombok.*;
import org.springframework.lang.Nullable;

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

    public static String getStatusString(@Nullable SparqlEndpointStatus status) {
        if (status == null)
            return "unknown";
        else if (status.isActive())
            return "active";
        else
            return "inactive";
    }

    public static List<SparqlEndpointDTO> fromSparqlEndpointList(List<SparqlEndpoint> sparqlEndpointList) {
        return sparqlEndpointList.stream().map(SparqlEndpointDTO::fromSparqlEndpoint).collect(Collectors.toList());
    }

    public static SparqlEndpointDTO fromSparqlEndpoint(SparqlEndpoint sparqlEndpoint) {
        return SparqlEndpointDTO.builder()
                .url(sparqlEndpoint.getUrl())
                .name(sparqlEndpoint.getName())
                .status(getStatusString(
                        sparqlEndpoint.getSparqlEndpointStatuses() != null && sparqlEndpoint.getSparqlEndpointStatuses().size() > 0?
                            sparqlEndpoint.getSparqlEndpointStatuses().get(0) : null))
                .build();
    }
}
