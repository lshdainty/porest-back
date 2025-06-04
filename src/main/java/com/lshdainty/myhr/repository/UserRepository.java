package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(Long userId);
    List<User> findUsers();
    List<User> findUsersWithVacations();
}
