package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unife.sparql_endpoint_availability.utilities.StringListConverter;
import java.util.ArrayList;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import org.hibernate.annotations.ColumnDefault;

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
    
    @NotNull
    private String name;
    
    @NotNull
    @Convert(converter = StringListConverter.class)
    private List<String> defaultGraphIRIs = new ArrayList<>();
    
    @NotNull
    @Convert(converter = StringListConverter.class)
    private List<String> namedGraphIRIs = new ArrayList<>();

    @JsonManagedReference //Inclued in serialization
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "sparqlEndpoint",cascade = CascadeType.ALL)
    private List<SparqlEndpointStatus> sparqlEndpointStatuses;

    public interface OnlyURL {
        Long getId();
        String getServiceURL();
//        String getName();
//        List<String> getDefaultGraphIRIs();
//        List<String> getNamedGraphIRIs();
    }

    @JsonIgnore
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

    /**
     * @return the defaultGraphIRIs
     */
    public List<String> getDefaultGraphIRIs() {
        return defaultGraphIRIs;
    }

    /**
     * @param defaultGraphIRIs the defaultGraphIRIs to set
     */
    public void setDefaultGraphIRIs(List<String> defaultGraphIRIs) {
        this.defaultGraphIRIs = defaultGraphIRIs;
    }

    /**
     * @return the namedGraphIRIs
     */
    public List<String> getNamedGraphIRIs() {
        return namedGraphIRIs;
    }

    /**
     * @param namedGraphIRIs the namedGraphIRIs to set
     */
    public void setNamedGraphIRIs(List<String> namedGraphIRIs) {
        this.namedGraphIRIs = namedGraphIRIs;
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
                this.serviceURL.equals(other.serviceURL);
    }
}
