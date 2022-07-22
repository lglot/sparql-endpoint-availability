package it.unife.sparql_endpoint_availability.model.management;

import it.unife.sparql_endpoint_availability.model.entity.AppGrantedAuthority;
import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface AppUserManagement extends UserDetailsService {

}
