package com.example.cowrite;

import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFind() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("zse123sdaw");
        user.setEmail("test@example.com");

        userRepository.save(user);

        User found = userRepository.findByEmail("test@example.com").orElse(null);
        assertThat(found).isNotNull();
        Assertions.assertNotNull(found);
        assertThat(found.getEmail()).isEqualTo("test@example.com");
    }
}
