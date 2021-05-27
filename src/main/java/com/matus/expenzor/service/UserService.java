package com.matus.expenzor.service;

import com.matus.expenzor.dto.user.UserProfileDto;
import com.matus.expenzor.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User saveUser(User user);

    User updateExistingUser(User user, UserProfileDto userProfileDto);

    void deleteUserById(Long id);

    Optional<User> findByUserName(String userName);

    Integer fetchUserId(String username);

    UserProfileDto userToUserProfileDto(User user);

    Iterable<User> findAllUsers();

}
