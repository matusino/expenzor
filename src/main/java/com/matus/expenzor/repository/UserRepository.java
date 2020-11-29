package com.matus.expenzor.repository;

import com.matus.expenzor.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUserName(String userName);

    @Query("SELECT id FROM User WHERE user_name = :user_name")
    Integer fetchUserId(String user_name);
}
