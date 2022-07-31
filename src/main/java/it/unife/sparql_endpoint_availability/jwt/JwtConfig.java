package it.unife.sparql_endpoint_availability.jwt;

import com.google.common.net.HttpHeaders;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

    private String secret;
    private int expirationTimeAfertDays;
    private String prefix;

    public JwtConfig() {
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpirationTimeAfertDays() {
        return expirationTimeAfertDays;
    }

    public void setExpirationTimeAfertDays(int expirationTimeAfertDays) {
        this.expirationTimeAfertDays = expirationTimeAfertDays;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

}
