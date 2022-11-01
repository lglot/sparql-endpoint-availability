package it.unife.sparql_endpoint_availability.dto;

import it.unife.sparql_endpoint_availability.model.entity.SparqlEndpoint;
import lombok.*;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointRequest {
    private String url;
    private String name;

    public static SparqlEndpoint toSparqlEndpoint(SparqlEndpointRequest sparqlEndpointRequest) {
        return SparqlEndpoint.builder()
                .url(sparqlEndpointRequest.getUrl())
                .name(sparqlEndpointRequest.getName())
                .build();
    }
}
