package com.matus.expenzor.controller;

import com.matus.expenzor.dto.user.PasswordDTO;
import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/get/{username}")
    public ResponseEntity getUserByUserName(@PathVariable String username){
        Optional <User> user = userService.findByUserName(username);
        if(user.isPresent()){
            return new ResponseEntity<>(userService.userToUserProfileDto(user.get()), HttpStatus.OK);
        }else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/update/{username}")
    public ResponseEntity updateUserProfile(@PathVariable String username, @Valid @RequestBody UserProfileDto userProfileDto,
                                            BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }else {
            Optional <User> userDB = userService.findByUserName(username);
            if(userDB.isPresent()){
                userService.updateExistingUser(userDB.get(), userProfileDto);
            }else {
                throw new UsernameNotFoundException("User Not found");
            }
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password/{username}")
    public ResponseEntity changePassword(@PathVariable String username, @Valid @RequestBody PasswordDTO passwordDTO,
                                         BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }else {
            Optional <User> userDB = userService.findByUserName(username);
            if(userDB.isPresent()){
                if(authenticationService.matchPassword(userDB.get(),passwordDTO) &&
                        passwordDTO.getNewPassword().equals(passwordDTO.getConfirmNewPassword())){
                    userDB.get().setPassword(passwordDTO.getNewPassword());
                    userService.saveUser(userDB.get());
                }else {
                    return ResponseEntity.badRequest().build();
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }
}
