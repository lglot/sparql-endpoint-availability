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
@Profile("h2_memory")
@PropertySource("classpath:persistence-h2_memory.properties")
class H2MemoryConfig {

}

@Configuration
@Profile("mysql")
@PropertySource("classpath:persistence-mysql.properties")
class MysqlConfig {
}

//postgres
@Configuration
@Profile("postgres")
@PropertySource("classpath:persistence-postgres.properties")
class PostgresConfig {
}


