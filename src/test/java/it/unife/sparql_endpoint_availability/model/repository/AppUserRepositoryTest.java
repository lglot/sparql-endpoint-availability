package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@DataJpaTest
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository userRepository;


//    @AfterEach
//    void deleteAll() {
//        if(userRepository.existsByUsername(appUser.getUsername()))
//            userRepository.deleteByUsername(appUser.getUsername());
//        if(userRepository.existsByUsername(appUser2.getUsername()))
//            userRepository.deleteByUsername(appUser2.getUsername());
//    }

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
        assertTrue(userList.size() >= 2);

        //verifico che gli username siano in lista
        assertTrue(userList.stream().anyMatch(user -> user.getUsername().equals("username")));
        assertTrue(userList.stream().anyMatch(user -> user.getUsername().equals("username2")));
    }

}