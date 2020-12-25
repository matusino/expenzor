package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import com.matus.expenzor.repository.UserRepository;
import com.matus.expenzor.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User updateExistingUser(User user, UserProfileDto userProfileDto) {
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setGender(userProfileDto.getGender());
        user.setCity(userProfileDto.getCity());
        user.setAge(userProfileDto.getAge());
        user.setPhoneNumber(userProfileDto.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public UserProfileDto userToUserProfileDto(User user) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setEmail(user.getEmailAddress());
        userProfileDto.setPhoneNumber(user.getPhoneNumber());
        userProfileDto.setGender(user.getGender());
        userProfileDto.setCity(user.getCity());
        userProfileDto.setAge(user.getAge());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setUsername(user.getUserName());
        return userProfileDto;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Integer fetchUserId(String username) {
        return userRepository.fetchUserId(username);
    }
}
