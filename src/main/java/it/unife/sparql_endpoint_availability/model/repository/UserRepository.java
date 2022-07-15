package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

}