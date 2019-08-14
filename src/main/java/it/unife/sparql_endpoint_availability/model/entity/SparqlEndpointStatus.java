package it.unife.sparql_endpoint_availability.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SparqlEndpointStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long statusId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="sparql_endpoint_id")
    private SparqlEndpoint sparqlEndpoint;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date queryDate;

    private boolean active;

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
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
