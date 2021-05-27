package com.matus.expenzor.controllerIT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matus.expenzor.BaseTest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithUserDetails("testUserName")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTestIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @PostConstruct
    void initUserDetails(){
        User user = new User();
        user.setUserName("testUserName");
        user.setPassword("testPassword");
        user.setEmailAddress("test@email.com");
        userService.saveUser(user);
    }

    @Test
    void shouldGetUserByUserName() throws Exception {
        mockMvc.perform(get("/users/{username}","testUserName")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is("testUserName")));
    }

    @Test
    void shouldUpdateUserProfile() throws Exception {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUsername("testUserName");
        userProfileDto.setEmail("test@email.com");
        userProfileDto.setFirstName("newFirstName");

        mockMvc.perform(patch("/users/{username}","testUserName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userProfileDto)))
                .andExpect(status().isOk());

        Optional<User> updatedUser = userService.findByUserName("testUserName");
        assertThat(updatedUser.get().getFirstName()).isEqualTo(userProfileDto.getFirstName());
    }

    @Test
    void shouldChangePassword() throws Exception {
        initDbUser();
        PasswordDTO newPassword = new PasswordDTO("testPassword", "newPassword","newPassword");
        mockMvc.perform(patch("/users/password/{username}", "userName")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newPassword)))
                .andExpect(status().isOk());
        Optional<User> updatedUser = userService.findByUserName("userName");
        assertThat(passwordEncoder.matches(newPassword.getNewPassword(), updatedUser.get().getPassword()));
    }

    @Test
    void shouldNotChangePasswordReturn400() throws Exception {
        initDbUser();
        PasswordDTO newPassword = new PasswordDTO("incorrectOldPassword", "newPassword","newPassword");
        MvcResult result = mockMvc.perform(patch("/users/password/{username}", "userName")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newPassword)))
                .andExpect(status().isBadRequest()).andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Password do not match");
    }

    @Test
    void shouldDeleteUserById() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());
        List<User> userList = new ArrayList<>();
        userService.findAllUsers().forEach(userList::add);
        assertThat(userList).isEmpty();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initDbUser(){
        RegisterRequest registerRequest = new RegisterRequest("userName","test@email.com","testPassword");
        authenticationService.signUp(registerRequest);
    }
}
