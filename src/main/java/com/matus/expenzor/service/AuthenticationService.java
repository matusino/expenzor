package com.matus.expenzor.service;

import com.matus.expenzor.dto.auth.AuthenticationResponse;
import com.matus.expenzor.dto.auth.LoginRequest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.dto.user.UserProfile;
import com.matus.expenzor.model.User;
import org.springframework.stereotype.Service;

import java.security.KeyException;

@Service
public interface AuthenticationService {

    void signUp(RegisterRequest registerRequest);

    AuthenticationResponse login(LoginRequest loginRequest) throws KeyException;

    boolean matchPassword(User user, UserProfile userProfile);
}
