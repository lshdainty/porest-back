package com.lshdainty.porest.work.domain;

import com.lshdainty.porest.common.domain.AuditingFields;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // -> protected Order() {}와 동일한 의미 (롬복으로 생성자 막기)
@Table(name = "work_history")
public class WorkHistory extends AuditingFields {
    /**
     * 이력 관리용 시퀀스
     */
    @Id @GeneratedValue
    @Column(name = "work_history_seq")
    private Long seq;

    /**
     * 업무 날짜
     */
    @Column(name = "work_date")
    private LocalDate date;

    /**
     * 근무자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_user")
    private User user;

    /**
     * 업무 그룹
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_group")
    private WorkCode group;

    /**
     * 업무 파트
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_part")
    private WorkCode part;

    /**
     * 업무 분류
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_class")
    private WorkCode classes;

    /**
     * 업무 시간
     */
    @Column(name = "work_hour")
    private BigDecimal hours;

    /**
     * 업무 내용
     */
    @Column(name = "work_content")
    private String content;

    /**
     * 삭제 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted")
    private YNType isDeleted;
}
