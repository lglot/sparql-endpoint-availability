package it.unife.sparql_endpoint_availability.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sparql.endpoint.status")
public class SparqlEndpointStatusConfig {

    /* STATUS OF ENDPOINTS */

    private String active;
    private String inactiveLessday;
    private String inactiveLessweek;
    private String inactiveMoreweek;
    private String generalInactive;

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

    public String getGeneralInactive() {
        return generalInactive;
    }

    public void setGeneralInactive(String generalInactive) {
        this.generalInactive = generalInactive;
    }

    public String getInactiveLessday() {
        return inactiveLessday;
    }

    public void setInactiveLessday(String inactiveLessday) {
        this.inactiveLessday = inactiveLessday;
    }
}
