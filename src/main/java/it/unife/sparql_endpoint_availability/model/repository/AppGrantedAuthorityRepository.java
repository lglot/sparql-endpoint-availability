package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.AppGrantedAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppGrantedAuthorityRepository extends JpaRepository<AppGrantedAuthority, Long> {

    boolean existsByRole(String role);
}