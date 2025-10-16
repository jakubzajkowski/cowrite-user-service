package com.example.cowrite;

import com.example.cowrite.entity.User;
import com.example.cowrite.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CowriteApplicationTests extends AbstractTestContainers {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testSimplePutAndGet() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("xd123@gmail.com");
        user.setPassword("hashedpassword");
        userRepository.save(user);

        User retrievedUser = userRepository.findByEmail("xd123@gmail.com").orElse(null);

        Assertions.assertNotNull(retrievedUser);
        assertThat(retrievedUser.getUsername().equals("testuser"));
    }
}
