package com.lshdainty.myhr.repository;

import com.lshdainty.myhr.domain.User;

import java.util.List;

public interface UserRepository {
    void save(User user);
    User findById(Long userId);
    List<User> findUsers();
    List<User> findUsersWithVacations();
}
