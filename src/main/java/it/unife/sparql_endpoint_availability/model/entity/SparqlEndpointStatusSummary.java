package it.unife.sparql_endpoint_availability.model.entity;

//Classe DTO, serve per trasferire informazioni degli sparrql enpoint al client
public class SparqlEndpointStatusSummary {

    private String URL;

    /*this variable indicates whether the sparql is active,
    or is inactive for less or more than a week*/
    private String statusString;

    private double uptimeLast24h;
    private double uptimelast7d;

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public double getUptimeLast24h() {
        return uptimeLast24h;
    }

    public void setUptimeLast24h(double uptimeLast24h) {
        this.uptimeLast24h = uptimeLast24h;
    }

    public double getUptimelast7d() {
        return uptimelast7d;
    }

    public void setUptimelast7d(double uptimelast7d) {
        this.uptimelast7d = uptimelast7d;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
