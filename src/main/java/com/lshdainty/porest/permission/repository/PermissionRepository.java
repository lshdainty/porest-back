package com.lshdainty.porest.permission.repository;

import com.lshdainty.porest.permission.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
