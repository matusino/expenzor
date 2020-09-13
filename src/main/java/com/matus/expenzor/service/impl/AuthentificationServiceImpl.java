package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.LoginRequest;
import com.matus.expenzor.dto.RegisterRequest;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class AuthentificationServiceImpl implements AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthentificationServiceImpl(PasswordEncoder passwordEncoder, UserService userService, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public void signUp(RegisterRequest registerRequest) {
        User user = new User();
        user.setuUserName(registerRequest.getUserName());
        user.setEmailAddress(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userService.saveUser(user);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName()
                ,loginRequest.getPassword()));
    }
}
