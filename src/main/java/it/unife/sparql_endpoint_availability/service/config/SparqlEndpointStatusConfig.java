package it.unife.sparql_endpoint_availability.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sparql.endpoint.status")
public class SparqlEndpointStatusConfig {

    /* STATUS OF ENDPOINTS */

    private String active;
    private String inactiveLessweek;
    private String inactiveMoreweek;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getInactiveLessweek() {
        return inactiveLessweek;
    }

    public void setInactiveLessweek(String inactiveLessweek) {
        this.inactiveLessweek = inactiveLessweek;
    }

    public String getInactiveMoreweek() {
        return inactiveMoreweek;
    }

    public void setInactiveMoreweek(String inactiveMoreweek) {
        this.inactiveMoreweek = inactiveMoreweek;
    }
}
