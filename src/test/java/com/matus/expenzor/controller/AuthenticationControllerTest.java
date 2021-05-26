package com.matus.expenzor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matus.expenzor.dto.auth.LoginRequest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.dto.auth.RegisterResponse;
import com.matus.expenzor.security.JwtProvider;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterUserWithValidInput() throws Exception {
        RegisterRequest request = new RegisterRequest("testTest", "correct@format.com", "1234567");
        MvcResult result = mockMvc.perform(post("/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<RegisterRequest> captor = ArgumentCaptor.forClass(RegisterRequest.class);
        verify(authenticationService, times(1)).signUp(captor.capture());

        assertThat(captor.getValue().getUsername()).isEqualTo(request.getUsername());
        assertThat(captor.getValue().getEmail()).isEqualTo(request.getEmail());
        assertThat(result.getResponse().getContentAsString()).isEqualTo("User Registered");
    }

    @Test
    @DisplayName("Test should fail because of short length of username")
    void shouldFailWithInvalidUser() throws Exception {
        RegisterRequest request = new RegisterRequest("short", "email@format.com", "1234567");
        mockMvc.perform(post("/auth/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("*Your username must have at least 6 characters")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testUserName", "testPassword");
        RegisterResponse response = new RegisterResponse("dummyToken", "testUserName");

        Mockito.when(authenticationService.login(loginRequest)).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authToken").value(response.getAuthToken()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(response.getUsername()))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}