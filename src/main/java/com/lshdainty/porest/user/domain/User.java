package com.lshdainty.porest.user.domain;

import com.lshdainty.porest.company.type.OriginCompanyType;
import com.lshdainty.porest.user.type.RoleType;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.vacation.domain.Vacation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // -> protected Order() {}와 동일한 의미 (롬복으로 생성자 막기)
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private String id; // 유저 아이디

    @Column(name = "user_pwd")
    private String pwd; // 유저 비번

    @Column(name = "user_name")
    private String name; // 유저 이름

    @Column(name = "user_email")
    private String email; // 유저 이메일

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private RoleType role; //

    @Column(name = "user_birth")
    private String birth; // 유저 생일

    @Column(name = "user_work_time")
    private String workTime; // 유연근무제

    @Enumerated(EnumType.STRING)
    @Column(name = "user_origin_company")
    private OriginCompanyType company; // 유저 원소속 회사

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "profile_uuid")
    private String profileUUID;

    @Enumerated(EnumType.STRING)
    @Column(name = "lunar_yn")
    private YNType lunarYN; // 음력여부

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn")
    private YNType delYN; // 삭제여부

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)   // JPA에서는 mappedBy는 읽기 전용
    private List<UserProvider> providers = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Vacation> vacations = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserVacationPolicy> userVacationPolicies = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserDepartment> userDepartments = new ArrayList<>();

    /**
     * 유저 생성 함수<br>
     * Entity의 경우 Setter없이 Getter만 사용<br>
     * 해당 메소드를 통해 유저 생성할 것
     *
     * @return User
     */
    public static User createUser(String id, String pwd, String name, String email, String birth,
                                  OriginCompanyType company, String workTime,
                                  YNType lunarYN, String profileName, String profileUUID) {
        User user = new User();
        user.id = id;
        user.pwd = pwd;
        user.name = name;
        user.email = email;
        user.role = RoleType.USER;
        user.birth = birth;
        user.company = company;
        user.workTime = workTime;
        user.lunarYN = lunarYN;
        user.profileName = profileName;
        user.profileUUID = profileUUID;
        user.delYN = YNType.N;
        return user;
    }

    public static User createUser(String id) {
        User user = new User();
        user.id = id;
        user.delYN = YNType.N;
        return user;
    }

    /**
     * 유저 수정 함수<br>
     * Entity의 경우 Setter없이 Getter만 사용<br>
     * 해당 메소드를 통해 유저 수정할 것
     */
    public void updateUser(String name, String email, RoleType role, String birth,
                           OriginCompanyType company, String workTime,
                           YNType lunarYN, String profileName, String profileUUID) {
        if (!Objects.isNull(name)) { this.name = name; }
        if (!Objects.isNull(email)) { this.email = email; }
        if (!Objects.isNull(role)) { this.role = role; }
        if (!Objects.isNull(birth)) { this.birth = birth; }
        if (!Objects.isNull(company)) { this.company = company; }
        if (!Objects.isNull(workTime)) { this.workTime = workTime; }
        if (!Objects.isNull(lunarYN)) { this.lunarYN = lunarYN; }
        if (!Objects.isNull(profileName)) { this.profileName = profileName; }
        if (!Objects.isNull(profileUUID)) { this.profileUUID = profileUUID; }
    }

    /**
     * 유저 삭제 함수<br>
     * Entity의 경우 Setter없이 Getter만 사용<br>
     * 해당 메소드를 통해 유저 삭제할 것
     */
    public void deleteUser() {
        this.delYN = YNType.Y;
    }

    /* 비즈니스 편의 메소드 */
    /**
     * 사용자의 workTime에 맞춰<br>
     * start, end가 담긴 배열을 반환하는 함수
     *
     * @return [startTime, endTime] array
     */
    public List<LocalTime> convertWorkTimeToLocalTime() {
        LocalTime start = LocalTime.of(0,0);
        LocalTime end = LocalTime.of(0,0);

        switch (getWorkTime()) {
            case "8 ~ 5":
                start = LocalTime.of(8,0);
                end = LocalTime.of(17,0);
                break;
            case "9 ~ 6":
                start = LocalTime.of(9,0);
                end = LocalTime.of(18,0);
                break;
            case "10 ~ 7":
                start = LocalTime.of(10,0);
                end = LocalTime.of(19,0);
                break;
        }

        return List.of(start, end);
    }

    /**
     * 날짜가 유저의 유연근무제에 맞춰<br>
     * 정상적으로 설정되어 있는지 확인하는 함수
     *
     * @param startTime 검사 대상 시작 시간
     * @param endTime 검사 대항 종료 시간
     * @return true, false 반환
     */
    public boolean isBetweenWorkTime(LocalTime startTime, LocalTime endTime) {
        List<LocalTime> workTimes = convertWorkTimeToLocalTime();
        return ((startTime.isAfter(workTimes.get(0)) || startTime.equals(workTimes.get(0))) && startTime.isBefore(workTimes.get(1))) &&
                (endTime.isAfter(workTimes.get(0)) && (endTime.isBefore(workTimes.get(1)) || endTime.equals(workTimes.get(1))));
    }
}
