package com.matus.expenzor.controller;

import com.matus.expenzor.dto.RegisterRequest;
import com.matus.expenzor.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authentificationService;

    public AuthenticationController(AuthenticationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @PostMapping("/singup")
    public ResponseEntity<String> signUp(@RequestBody RegisterRequest registerRequest){
        authentificationService.signUp(registerRequest);
        return new ResponseEntity<>("User Registered", HttpStatus.OK);
    }
}
