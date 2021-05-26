package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.auth.LoginRequest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.dto.auth.RegisterResponse;
import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.model.User;
import com.matus.expenzor.security.JwtProvider;
import com.matus.expenzor.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthentificationServiceImplTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserService userService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtProvider jwtProvider;

    @Captor
    ArgumentCaptor<RegisterRequest> captor;

    @InjectMocks
    AuthentificationServiceImpl authService;

    @Test
    void shouldSignUp() {
        //given
        RegisterRequest registerRequest = new RegisterRequest();
        //when
        authService.signUp(registerRequest);
        //then
        then(userService).should().saveUser(new User());

        verify(userService, times(1)).saveUser(new User());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());

    }

    @Test
    void login() {
        //given
        LoginRequest loginRequest = new LoginRequest("test", "test");
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        given(jwtProvider.generateToken(authentication)).willReturn("token");
        //when
        RegisterResponse response = authService.login(loginRequest);
        //then
        verify(jwtProvider, times(1)).generateToken(authentication);
        assertThat(response.getAuthToken().equalsIgnoreCase("token"));
        assertThat(response.getUsername().equalsIgnoreCase(loginRequest.getUsername()));

    }

    @Test
    void shouldMatchPassword() {
        //given
        User user = new User();
        user.setPassword("password");
        PasswordDTO passwordDTO = new PasswordDTO();
        passwordDTO.setPassword("password");

//        given(passwordEncoder.matches(user.getPassword(), passwordDTO.getPassword())).willReturn(true);
        //when
        boolean match = authService.matchPassword(user, passwordDTO);

        //then
        assertThat(match);
        verify(passwordEncoder, times(1)).matches(user.getPassword(), passwordDTO.getPassword());

    }

}