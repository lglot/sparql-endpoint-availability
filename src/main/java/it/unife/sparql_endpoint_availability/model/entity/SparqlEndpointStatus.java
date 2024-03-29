package it.unife.sparql_endpoint_availability.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import it.unife.sparql_endpoint_availability.config.AppConfig;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sparql_endpoint_status")
public class SparqlEndpointStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference // Omitted from serialization
    @ToString.Exclude
    private SparqlEndpoint sparqlEndpoint;

    @Basic(optional = false)
    //@Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(timezone = AppConfig.LOCAL_TIMEZONE)
    private LocalDateTime queryDate;

    private boolean active;
    
}
