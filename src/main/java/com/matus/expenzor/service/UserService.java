package com.matus.expenzor.service;

import com.matus.expenzor.dto.user.UserProfile;
import com.matus.expenzor.model.User;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    User saveUser(User user);

    @Nullable
    User findUserById(Long id);

    @Nullable
    List<User> getAllUsers();

    User updateExistingUser(User user, UserProfile userProfile);

    void deleteUserById(Long id);

    Optional<User> findByUserName(String userName);

    UserProfile updateUserProfile(String userName);

    UserProfile getUserProfile(String userName);

    Integer fetchUserId(String username);

}
