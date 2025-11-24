package com.lshdainty.porest.permission.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {

    @Id
    @Column(name = "permission_name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "resource")
    private String resource;

    @Column(name = "action")
    private String action;

    public static Permission create(String name, String description, String resource, String action) {
        Permission permission = new Permission();
        permission.name = name;
        permission.description = description;
        permission.resource = resource;
        permission.action = action;
        return permission;
    }
}
