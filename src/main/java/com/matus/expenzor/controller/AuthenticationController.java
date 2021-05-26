package com.matus.expenzor.controller;

import com.matus.expenzor.dto.auth.RegisterResponse;
import com.matus.expenzor.dto.auth.LoginRequest;
import com.matus.expenzor.dto.auth.RegisterRequest;
import com.matus.expenzor.service.AuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.KeyException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<Object> signUp(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error(String.valueOf(errors));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        authenticationService.signUp(registerRequest);
        return new ResponseEntity<>("User Registered", HttpStatus.OK);
    }

    @PostMapping("/login")
    public RegisterResponse login(@RequestBody LoginRequest loginRequest) throws KeyException {
        return authenticationService.login(loginRequest);
    }
}
