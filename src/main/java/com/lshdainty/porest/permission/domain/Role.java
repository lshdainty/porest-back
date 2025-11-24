package com.lshdainty.porest.permission.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "role_name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_name"),
            inverseJoinColumns = @JoinColumn(name = "permission_name")
    )
    private List<Permission> permissions = new ArrayList<>();

    public static Role create(String name, String description) {
        Role role = new Role();
        role.name = name;
        role.description = description;
        return role;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
}
