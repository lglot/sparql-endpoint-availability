package it.unife.sparql_endpoint_availability.model.entity;

import lombok.*;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SparqlEndpointDTO {

    private String url;
    private String name;
    private boolean active;
}
