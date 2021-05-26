package com.matus.expenzor.repository;

import com.matus.expenzor.BaseTest;
import com.matus.expenzor.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends BaseTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserName("testUserName");
        user.setEmailAddress("email@test.com");
        user.setPassword("testPassword");
        userRepository.save(user);
    }

    @Test
    void shouldFindByUserName(){
        Optional<User> foundUser = userRepository.findByUserName("testUserName");
        assertThat(foundUser.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(user);
    }

    @Test
    void shouldFetchUserIdByUserName(){
        Integer userId = userRepository.fetchUserId("testUserName");
        assertThat(userId).isEqualTo(user.getId().intValue());
    }
}