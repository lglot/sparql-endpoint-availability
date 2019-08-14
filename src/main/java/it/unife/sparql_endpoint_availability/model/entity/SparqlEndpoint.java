package it.unife.sparql_endpoint_availability.model.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class SparqlEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String serviceURL;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "sparqlEndpoint")
    private List<SparqlEndpointStatus> sparqlEndpointStatuses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public List<SparqlEndpointStatus> getSparqlEndpointStatuses() {
        return sparqlEndpointStatuses;
    }

    public void setSparqlEndpointStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        this.sparqlEndpointStatuses = sparqlEndpointStatuses;
    }
}
