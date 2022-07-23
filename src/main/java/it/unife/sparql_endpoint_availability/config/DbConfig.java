package it.unife.sparql_endpoint_availability.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

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


