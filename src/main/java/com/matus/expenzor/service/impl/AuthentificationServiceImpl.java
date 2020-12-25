package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.auth.RegisterResponse;
import com.matus.expenzor.dto.auth.LoginRequest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.model.User;
import com.matus.expenzor.security.JwtProvider;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class AuthentificationServiceImpl implements AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthentificationServiceImpl(PasswordEncoder passwordEncoder, UserService userService, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setEmailAddress(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userService.saveUser(user);
    }

    @Override
    public RegisterResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername()
                ,loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return new RegisterResponse(token,loginRequest.getUsername());
    }

    @Override
    public boolean matchPassword(User user , PasswordDTO passwordDTO) {
        String dbPassword = user.getPassword();
        String oldPassword = passwordDTO.getPassword();

        return passwordEncoder.matches(oldPassword, dbPassword);
    }
}
