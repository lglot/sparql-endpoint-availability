package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;


@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedEntityGraph(name = "SparqlEndpoint.detail", attributeNodes = @NamedAttributeNode("sparqlEndpointStatuses"))
public class SparqlEndpoint {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String url;
    @NotNull
    private String name;

    @JsonManagedReference // Inclued in serialization
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sparqlEndpoint", cascade = CascadeType.ALL)
    private List<SparqlEndpointStatus> sparqlEndpointStatuses;

    public SparqlEndpoint(Long id ,String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }
    public SparqlEndpoint(String url, String name) {
        this.url = url;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SparqlEndpoint)) {
            return false;
        }
        SparqlEndpoint other = (SparqlEndpoint) o;
        return this.name.equals(other.name) &&
                this.url.equals(other.url);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.url);
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }

    //get last check date of the endpoint by sorting the list of statuses by date and taking the first element
    public LocalDateTime getLastCheckDate() {
        if (sparqlEndpointStatuses == null || sparqlEndpointStatuses.isEmpty()) {
            return null;
        }
        sparqlEndpointStatuses.sort(Comparator.comparing(SparqlEndpointStatus::getQueryDate));
        return sparqlEndpointStatuses.get(0).getQueryDate();
    }

    //get last check date of the endpoint when sorted by date
    public LocalDateTime getLastCheckDate(boolean descending) {
        if (sparqlEndpointStatuses.isEmpty()) {
            return null;
        }
        if (descending) {
            return sparqlEndpointStatuses.get(0).getQueryDate();
        } else {
            return sparqlEndpointStatuses.get(sparqlEndpointStatuses.size() - 1).getQueryDate();
        }
    }




}
