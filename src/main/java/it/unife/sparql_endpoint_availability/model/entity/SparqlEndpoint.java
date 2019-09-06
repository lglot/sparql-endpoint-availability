package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@NamedEntityGraph(name = "SparqlEndpoint.detail",
        attributeNodes = @NamedAttributeNode("sparqlEndpointStatuses"))
public class SparqlEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String serviceURL;

    @JsonManagedReference //Inclued in serialization
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "sparqlEndpoint",cascade = CascadeType.ALL)
    private List<SparqlEndpointStatus> sparqlEndpointStatuses;

    public interface OnlyURL {
        Long getId();
        String getServiceURL();
    }

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

    @Override
    public String toString(){
        return "1: " + this.getId() + " 2: " + this.getServiceURL();
    }
}
