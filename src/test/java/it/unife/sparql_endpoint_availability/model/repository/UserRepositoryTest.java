package it.unife.sparql_endpoint_availability.model.repository;

import it.unife.sparql_endpoint_availability.config.TestConfiguration;
import it.unife.sparql_endpoint_availability.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void should_save_user() {
        User user = User.builder().username("username").build();
        userRepository.save(user);
        assertNotNull(user.getId());
    }

}