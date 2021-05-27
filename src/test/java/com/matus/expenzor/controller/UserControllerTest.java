package com.matus.expenzor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.security.JwtProvider;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import com.matus.expenzor.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;
import static org.hamcrest.Matchers.is;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    private User user;

    @Captor
    ArgumentCaptor<String> argumentCaptor;

    @BeforeEach
    void setUp() {
        user = new User("testUserName", "email@address.com", "password", new ArrayList<>());
        user.setId(1L);

    }

    @Test
    @WithMockUser(username="testUserName",password = "pass")
    void shouldGetUserByUserName() throws Exception {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUsername("testUserName");
        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(user));
        given(userService.userToUserProfileDto(user)).willReturn(userProfileDto);

        mockMvc.perform(get("/users/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is(user.getUserName())))
                .andExpect(status().isOk());

        then(userService).should().findByUserName(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualToIgnoringCase(user.getUserName());
        verify(userService, times(1)).findByUserName(user.getUserName());
    }

    @Test
    @WithMockUser(username="testUserName",password = "pass")
    void shouldRestrictAndNotGetUserByUserName() throws Exception {
        given(userService.findByUserName(any(String.class))).willReturn(Optional.empty());
        mockMvc.perform(get("/users/{username}","incorrectUserName"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @WithMockUser(username="testUserName",password = "pass")
    void shouldUpdateUserProfile() throws Exception {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setUsername(user.getUserName());
        userProfile.setEmail(user.getEmailAddress());
        userProfile.setFirstName("newFirstName");

        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(user));
        mockMvc.perform(patch("/users/{username}", user.getUserName())
                .content(asJsonString(userProfile))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        then(userService).should().updateExistingUser(user,userProfile);
    }

    @Test
    @WithMockUser(username="testUserName",password = "pass")
    void shouldFailUpdateUserProfileInvalidInput() throws Exception {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setUsername(null);
        userProfile.setEmail(user.getEmailAddress());
        userProfile.setFirstName("newFirstName");

        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(user));
        mockMvc.perform(patch("/users/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userProfile))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Username cannot be null")))
                .andExpect(status().is4xxClientError());
    }
    @Test
    @DisplayName("Invalid username should fail, return empty")
    @WithMockUser(username="testUserName",password = "pass")
    void shouldFailUpdateUserNotFound() throws Exception {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.setUsername(user.getUserName());
        userProfile.setEmail(user.getEmailAddress());
        userProfile.setFirstName("newFirstName");

        given(userService.findByUserName(any(String.class))).willReturn(Optional.empty());
        mockMvc.perform(patch("/users/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userProfile))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="testUserName")
    void shouldChangePassword() throws Exception {
        PasswordDTO password = new PasswordDTO("password","newPassword","newPassword");
        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(user));
        given(authenticationService.matchPassword(user, password)).willReturn(true);

        mockMvc.perform(patch("/users/password/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(password)))
                .andExpect(status().isOk());

        then(authenticationService).should().changePassword(user, password.getNewPassword());
    }
    @Test
    @WithMockUser(username="testUserName")
    void shouldFailChangePassword() throws Exception {
        PasswordDTO password = new PasswordDTO("password","newPassword","newPassword");

        mockMvc.perform(patch("/users/password/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(password)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="testUserName",password = "password")
    void shouldFailChangePasswordOldPasswordDoNotMatchNewPassword() throws Exception {
        PasswordDTO password = new PasswordDTO("password","newPassword","newPassword");
        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(user));

        mockMvc.perform(patch("/users/password/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(password)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="testUserName",password = "password")
    void shouldFailPasswordChangeInvalidInput() throws Exception {
        PasswordDTO password = new PasswordDTO(null,"newPassword","newPassword");

        mockMvc.perform(patch("/users/password/{username}", user.getUserName())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(password)))
                .andExpect(content().string(containsString("*Please provide your password")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username="testUserName")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}