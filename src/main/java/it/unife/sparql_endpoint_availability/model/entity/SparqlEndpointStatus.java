package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import it.unife.sparql_endpoint_availability.config.AppConfig;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SparqlEndpointStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference // Omitted from serialization
    private SparqlEndpoint sparqlEndpoint;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(timezone = AppConfig.LOCAL_TIMEZONE)
    private Date queryDate;

    private boolean active;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public SparqlEndpoint getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(SparqlEndpoint sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
