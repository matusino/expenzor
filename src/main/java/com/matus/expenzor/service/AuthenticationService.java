package com.matus.expenzor.service;

import com.matus.expenzor.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    void signUp(RegisterRequest registerRequest);
}
