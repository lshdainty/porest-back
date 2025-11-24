package com.lshdainty.porest.permission.repository;

import com.lshdainty.porest.permission.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
