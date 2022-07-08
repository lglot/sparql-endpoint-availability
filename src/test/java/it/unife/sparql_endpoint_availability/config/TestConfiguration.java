package it.unife.sparql_endpoint_availability.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.TestPropertySource;


@Configuration
@EnableJpaAuditing
@TestPropertySource(locations = "classpath:test.properties")
public class TestConfiguration {

}

