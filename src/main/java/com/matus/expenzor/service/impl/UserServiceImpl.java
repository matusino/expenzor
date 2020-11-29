package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.user.UserProfile;
import com.matus.expenzor.model.User;
import com.matus.expenzor.repository.UserRepository;
import com.matus.expenzor.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    @Override
    public User updateExistingUser(User user, UserProfile userProfile) {
        user.setFirstName(userProfile.getFirstName());
        user.setLastName(userProfile.getLastName());
        user.setGender(userProfile.getGender());
        user.setCity(userProfile.getCity());
        user.setAge(userProfile.getAge());
        user.setPhoneNumber(userProfile.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserProfile updateUserProfile(String userName) {
        User user = userRepository.findByUserName(userName).orElse(null);
        UserProfile userProfile = new UserProfile();
        if(user != null){
            userProfile.setUserName(user.getUserName());
            userProfile.setEmail(user.getEmailAddress());
            userProfile.setFirstName(user.getFirstName());
            userProfile.setLastName(user.getLastName());
            userProfile.setAge(user.getAge());
            userProfile.setCity(user.getCity());
            userProfile.setGender(user.getGender());
            userProfile.setPhoneNumber(user.getPhoneNumber());
        }else {
            throw new UsernameNotFoundException("User not found is database");
        }
        return userProfile;
    }

    @Override
    public UserProfile getUserProfile(String userName) {
        User user = userRepository.findByUserName(userName).orElse(null);
        UserProfile userProfile = new UserProfile();
        if(user != null){
            userProfile.setUserName(user.getUserName());
            userProfile.setEmail(user.getEmailAddress());
            userProfile.setFirstName(user.getFirstName());
            userProfile.setLastName(user.getLastName());
            userProfile.setAge(user.getAge());
            userProfile.setCity(user.getCity());
            userProfile.setGender(user.getGender());
            userProfile.setPhoneNumber(user.getPhoneNumber());
        }else {
            throw new UsernameNotFoundException("User not found is database");
        }
        return userProfile;
    }

    @Override
    public Integer fetchUserId(String username) {
        return userRepository.fetchUserId(username);
    }
}
