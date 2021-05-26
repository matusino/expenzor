package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUserName", "email@address.com", "password", new ArrayList<>());
        user.setId(1L);

    }

    @Test
    void shouldFindUserByUserName() {
        //Given
        given(userRepository.findByUserName(user.getUserName())).willReturn(Optional.of(user));
        //When
        Optional<User> foundUser = userService.findByUserName(user.getUserName());
        //Then
        then(userRepository).should().findByUserName(user.getUserName());
        assertNotNull(foundUser);
    }

    @Test
    void saveUser() {
        //given
        given(userRepository.save(any(User.class))).willReturn(user);
        //when
        User savedUser = userService.saveUser(user);
        //then
        then(userRepository).should().save(user);
        assertEquals(savedUser, user);
    }

    @Test
    void updateExistingUser() {
        //given
        UserProfileDto updatedUser = new UserProfileDto("testUserName","email@address.com",
                "newFirstName","newLastName","Male","New York","2121212",20);
        ///when
        userService.updateExistingUser(user, updatedUser);
        //then
        then(userRepository).should().save(user);
        verify(userRepository, times(1)).save(user);

        assertThat(user.getFirstName()).isEqualTo(updatedUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(updatedUser.getLastName());
        assertThat(user.getGender()).isEqualTo(updatedUser.getGender());
        assertThat(user.getCity()).isEqualTo(updatedUser.getCity());
        assertThat(user.getPhoneNumber()).isEqualTo(updatedUser.getPhoneNumber());
        assertThat(user.getAge()).isEqualTo(updatedUser.getAge());
        assertThat(user.getUserName()).isEqualTo(updatedUser.getUsername());
    }

    @Test
    void userToUserProfileDto() {
        //given
        UserProfileDto userProfileDto;
        //when
        userProfileDto = userService.userToUserProfileDto(user);
        //then
        assertThat(user.getUserName()).isEqualTo(userProfileDto.getUsername());
        assertThat(user.getEmailAddress()).isEqualTo(userProfileDto.getEmail());

    }

    @Test
    void deleteUserById() {
        //given
        //when
        userService.deleteUserById(1L);
        //then
        then(userRepository).should().deleteById(1L);
    }

    @Test
    void fetchUserId() {
        //given
        given(userService.fetchUserId(any(String.class))).willReturn(1);
        //when
        Integer id = userService.fetchUserId("userName");
        //
        then(userRepository).should().fetchUserId("userName");
        assertThat(id).isEqualTo(1);
        verify(userRepository, times(1)).fetchUserId("userName");
    }
}