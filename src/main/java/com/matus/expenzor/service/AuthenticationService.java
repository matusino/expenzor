package com.matus.expenzor.service;

import com.matus.expenzor.dto.AuthenticationResponse;
import com.matus.expenzor.dto.LoginRequest;
import com.matus.expenzor.dto.RegisterRequest;
import org.springframework.stereotype.Service;

import java.security.KeyException;

@Service
public interface AuthenticationService {
    void signUp(RegisterRequest registerRequest);

    AuthenticationResponse login(LoginRequest loginRequest) throws KeyException;
}
