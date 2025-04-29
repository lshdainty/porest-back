package com.lshdainty.myhr.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // -> protected Order() {}와 동일한 의미 (롬복으로 생성자 막기)
@Table(name = "deptop_vacation")
public class Vacation extends AuditingFields {
    @Id @GeneratedValue
    @Column(name = "vacation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no")
    @Setter
    private User user;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "vacation", cascade = CascadeType.ALL)
    private List<Schedule> schedules =  new ArrayList<>();

    @Column(name = "vacation_name")
    private String name;

    @Column(name = "vacation_desc")
    private String desc;

    @Enumerated(EnumType.STRING)
    @Column(name = "vacation_type")
    private VacationType type;

    @Column(name = "grant_time", precision = 7, scale = 4)
    private BigDecimal grantTime;

    @Column(name = "occur_date")
    private LocalDateTime occurDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "del_yn")
    private String delYN;

    // user 추가 연관관계 편의 메소드
    public void addUser(User user) {
        this.user = user;
        user.getVacations().add(this);
    }

    // 휴가 생성자 (setter말고 해당 메소드 사용할 것)
    public static Vacation createVacation(User user, String name, String desc, VacationType type, BigDecimal grantTime, LocalDateTime occurDate, LocalDateTime expiryDate, Long userNo, String clientIP) {
        Vacation vacation = new Vacation();
        vacation.addUser(user);
        vacation.name = name;
        vacation.desc = desc;
        vacation.type = type;
        vacation.grantTime = grantTime;
        vacation.occurDate = occurDate;
        vacation.expiryDate = expiryDate;
        vacation.delYN = "N";
        vacation.setCreated(userNo, clientIP);
        return vacation;
    }

    // 휴가 삭제 (setter말고 해당 메소드 사용할 것)
    public void deleteVacation(Long userNo, String clientIP) {
        this.delYN = "Y";
        this.setDeleted(LocalDateTime.now(), userNo, clientIP);
    }

    /* 비즈니스 편의 메소드 */

    /**
     * occurDate, expireDate를 비교하여
     * 발생일자가 만료일자 이전인지 확인
     *
     * @return true, false
     */
    public boolean isBeforeOccur() {
        return !getOccurDate().isBefore(getExpiryDate());
    }
}
