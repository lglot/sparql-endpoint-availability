package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository userRepository;

    @Test
    public void should_save_user() {
        AppUser appUser = AppUser.builder().username("username").build();
        userRepository.save(appUser);
        assertTrue(appUser.getId() > 0);
    }

    @Test
    public void should_find_user_by_username() {
        AppUser appUser = AppUser.builder().username("username").build();
        userRepository.save(appUser);
        Optional<AppUser> user2 = userRepository.findByUsername("username");
        assertTrue(user2.isPresent());
        assertEquals(appUser.getId(), user2.get().getId());
    }

    @Test
    public void should_find_all_users() {
        AppUser appUser = AppUser.builder().username("username").build();
        userRepository.save(appUser);
        AppUser appUser2 = AppUser.builder().username("username2").build();
        userRepository.save(appUser2);
        List<AppUser> userList = userRepository.findAll();
        assertTrue(userList.iterator().hasNext());
        assertTrue(userList.size() > 1);
    }

}