package com.matus.expenzor.service;

import com.matus.expenzor.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User saveUser(User user);

    User findUserById(Long id);

    List<User> getAllUsers();

    User updateExistingUser(User user);

    void deleteUserById(Long id);

}
