package it.unife.sparql_endpoint_availability.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("it.unife.sparql_endpoint_availability.model.repository")
public class DbConfig {

}

@Configuration
@Profile("h2_file")
@PropertySource("classpath:persistence-h2_file.properties")
class H2FileConfig {

}


@Configuration
@Profile("h2")
@PropertySource("classpath:persistence-h2.properties")
class H2MemoryConfig {

}

@Configuration
@Profile("mysql")
@PropertySource("classpath:persistence-mysql.properties")
class MysqlConfig {
}

//postgres
@Configuration
@Profile("postgresql")
@PropertySource("classpath:persistence-postgresql.properties")
class PostgresConfig {
}


