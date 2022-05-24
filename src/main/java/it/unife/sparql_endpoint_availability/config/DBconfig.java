package it.unife.sparql_endpoint_availability.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "it.unife.sparql_endpoint_availability.model.repository")
public class DBconfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] { "it.unife.sparql_endpoint_availability.model.entity" });
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        if (env.getProperty("spring.jpa.hibernate.ddl-auto") != null) {
            hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        }
        if (env.getProperty("spring.jpa.database-platform") != null) {
            hibernateProperties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        }
        if (env.getProperty("spring.jpa.show-sql") != null) {
            hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        }
        hibernateProperties.setProperty("hibernate.connection.pool_size", "1");
        hibernateProperties.setProperty("hibernate.connection.autocommit", "true");
        return hibernateProperties;
    }
}
