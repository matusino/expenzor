package com.matus.expenzor.controller;

import com.matus.expenzor.dto.AuthenticationResponse;
import com.matus.expenzor.dto.LoginRequest;
import com.matus.expenzor.dto.RegisterRequest;
import com.matus.expenzor.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authentificationService;

    public AuthenticationController(AuthenticationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest registerRequest){
        authentificationService.signUp(registerRequest);
        return new ResponseEntity<>("User Registered", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) throws KeyException {
        return authentificationService.login(loginRequest);
    }
}
