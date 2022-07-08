package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unife.sparql_endpoint_availability.utilities.StringListConverter;
import java.util.ArrayList;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;


@Entity
@NamedEntityGraph(name = "SparqlEndpoint.detail", attributeNodes = @NamedAttributeNode("sparqlEndpointStatuses"))
public class SparqlEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    private String url;

    @NotNull
    private String name;

    @JsonManagedReference // Inclued in serialization
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sparqlEndpoint", cascade = CascadeType.ALL)
    private List<SparqlEndpointStatus> sparqlEndpointStatuses;

    public SparqlEndpoint() {

    }
    public SparqlEndpoint(Long id ,String url, String name) {
        this.id = id;
        this.url = url;
        this.name = name;
    }
    public SparqlEndpoint(String url, String name) {
        this.url = url;
        this.name = name;
    }


    public interface OnlySparqlEndpoint {

        Long getId();

        String getUrl();

        String getName();

    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<SparqlEndpointStatus> getSparqlEndpointStatuses() {
        return sparqlEndpointStatuses;
    }

    public void setSparqlEndpointStatuses(List<SparqlEndpointStatus> sparqlEndpointStatuses) {
        this.sparqlEndpointStatuses = sparqlEndpointStatuses;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
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

    @Override
    public String toString() {
        return "SparqlEndpoint{" + "id=" + id + ", url=" + url + ", name=" + name + '}';
    }

}
