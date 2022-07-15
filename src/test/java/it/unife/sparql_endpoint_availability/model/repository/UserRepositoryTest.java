package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void should_save_user() {
        User user = User.builder().username("username").build();
        userRepository.save(user);
        assertTrue(user.getId() > 0);
    }

    @Test
    public void getUserTest() {
        User user = User.builder().username("username").build();
        userRepository.save(user);
        Optional<User> user2 = userRepository.findById(user.getId());
        assertTrue(user2.isPresent());
        assertEquals(user.getId(), user2.get().getId());
    }

    @Test
    public void getUserListTest() {
        User user = User.builder().username("username").build();
        userRepository.save(user);
        User user2 = User.builder().username("username2").build();
        userRepository.save(user2);
        Iterable<User> userList = userRepository.findAll();
        assertTrue(userList.iterator().hasNext());
    }

}