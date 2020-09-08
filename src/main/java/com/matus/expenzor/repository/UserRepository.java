package com.matus.expenzor.repository;

import com.matus.expenzor.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
