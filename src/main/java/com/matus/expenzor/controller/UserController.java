package com.matus.expenzor.controller;

import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping(value = "/{username}" ,produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity getUserByUserName(@PathVariable String username) {
        Optional<User> user = userService.findByUserName(username);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("content-type", "application/json");
        if (user.isPresent()) {
            return new ResponseEntity<>(userService.userToUserProfileDto(user.get()), responseHeaders, HttpStatus.OK);
        } else {
            log.error("User " + username + " not found");
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{username}")
    public ResponseEntity updateUserProfile(@PathVariable String username, @Valid @RequestBody UserProfileDto userProfileDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error(errors.toString());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Optional<User> userDB = userService.findByUserName(username);
        if (userDB.isPresent()) {
            userService.updateExistingUser(userDB.get(), userProfileDto);
            return ResponseEntity.ok().build();
        } else {
            log.error("User " + username + " not found");
            return ResponseEntity.notFound().build();
        }

    }

    @PatchMapping("/password/{username}")
    public ResponseEntity changePassword(@PathVariable String username, @Valid @RequestBody PasswordDTO passwordDTO,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error(errors.toString());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Optional<User> userDB = userService.findByUserName(username);
        if (userDB.isPresent()) {
            if (authenticationService.matchPassword(userDB.get(), passwordDTO) &&
                    passwordDTO.getNewPassword().equals(passwordDTO.getConfirmNewPassword())) {
                userDB.get().setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
                userService.saveUser(userDB.get());
                return ResponseEntity.ok().build();
            } else {
                log.error("Password change not successful. Passwords do not match");
                return new ResponseEntity<>("Password do not match", HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("User" + username + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id) {
        try{
            userService.deleteUserById(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return ResponseEntity.notFound().build();
        }
    }
}
