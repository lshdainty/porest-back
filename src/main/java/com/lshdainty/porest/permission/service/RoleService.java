package com.lshdainty.porest.permission.service;

import com.lshdainty.porest.permission.domain.Permission;
import com.lshdainty.porest.permission.domain.Role;
import com.lshdainty.porest.permission.repository.PermissionRepository;
import com.lshdainty.porest.permission.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(String roleName) {
        return roleRepository.findById(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public Role createRole(String roleName, String description) {
        if (roleRepository.existsById(roleName)) {
            throw new IllegalArgumentException("Role already exists: " + roleName);
        }
        return roleRepository.save(Role.create(roleName, description));
    }

    @Transactional
    public void updateRolePermissions(String roleName, List<String> permissionNames) {
        Role role = getRole(roleName);
        
        // Clear existing permissions (if needed, or just add/remove)
        // For simplicity, we might want to clear and re-add, or just add new ones.
        // Since Role.permissions is a List, we need to be careful. 
        // Let's assume we want to SET the permissions to exactly this list.
        
        // However, Role entity doesn't have a clearPermissions method exposed yet.
        // We might need to update Role entity or just handle it here.
        // Let's fetch all permissions first.
        
        List<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findById(name)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name)))
                .collect(Collectors.toList());

        // We need a way to set permissions. 
        // Let's assume we can modify the list directly if it's exposed, or add a method to Role.
        // Checking Role.java...
        // It has `getPermissions()` which returns `List<Permission>`. 
        // If it's a modifiable list, we can clear and add.
        
        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);
        
        roleRepository.save(role);
    }
    
    @Transactional
    public void deleteRole(String roleName) {
        if (!roleRepository.existsById(roleName)) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        roleRepository.deleteById(roleName);
    }
}
