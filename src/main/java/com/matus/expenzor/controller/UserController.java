package com.matus.expenzor.controller;

import com.matus.expenzor.dto.user.UserProfile;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.AuthenticationService;
import com.matus.expenzor.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }


    @RequestMapping("/get/user/{username}")
    public ResponseEntity getUserById(@PathVariable String username){
        UserProfile userDB = userService.getUserProfile(username); //doplne sem check keby je nahodou null
        return new ResponseEntity<>(userDB, HttpStatus.OK);
    }

    @PatchMapping("/update/{username}")
    public ResponseEntity updateUserProfile(@PathVariable String username, @RequestBody UserProfile userProfile){
        User userDB = userService.findByUserName(username).orElse(null);//null check
        if(userDB != null){
            userService.updateExistingUser(userDB, userProfile);
        }else {
            throw new UsernameNotFoundException("User Not found");
        }

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password/{username}")
    public ResponseEntity changePassword(@PathVariable String username, @RequestBody UserProfile userProfile){
        User user = userService.findByUserName(username).orElse(null);//null check
        if(authenticationService.matchPassword(user,userProfile)){
            user.setPassword(userProfile.getNewPassword());
            userService.saveUser(user);
        }else {
            return ResponseEntity.badRequest().build();//not sure about this
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-expense")
    public ResponseEntity getExpenseByUser(Principal principal){
        User user = userService.findByUserName(principal.getName()).orElse(null);
        if(user != null){
            return new ResponseEntity<>(user.getExpenses(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }


}
