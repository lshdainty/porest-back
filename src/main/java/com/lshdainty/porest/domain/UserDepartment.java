package com.lshdainty.porest.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // -> protected Order() {}와 동일한 의미 (롬복으로 생성자 막기)
@Table(name = "user_department")
public class UserDepartment extends AuditingFields {
    @Id @GeneratedValue
    @Column(name = "user_department_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @Setter
    private Department department;

    // user 추가 연관관계 편의 메소드
    public void addUser(User user) {
        this.user = user;
        user.getUserDepartments().add(this);
    }

    // department 추가 연관관계 편의 메소드
    public void addDepartment(Department department) {
        this.department = department;
        department.getUserDepartments().add(this);
    }
}
