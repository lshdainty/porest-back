package com.lshdainty.porest.work.domain;

import com.lshdainty.porest.common.domain.AuditingFields;
import com.lshdainty.porest.common.type.YNType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // -> protected Order() {}와 동일한 의미 (롬복으로 생성자 막기)
@Table(name = "work_code")
public class WorkCode extends AuditingFields {
    /**
     * 코드 관리용 시퀀스
     */
    @Id @GeneratedValue
    @Column(name = "work_code_seq")
    private Long seq;

    /**
     * 코드 이름
     */
    @Column(name = "work_code_name")
    private String name;

    /**
     * 부모 코드 (자기 참조)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    private WorkCode parent;

    /**
     * 정렬 순서
     */
    @Column(name = "order_seq")
    private Integer orderSeq;

    /**
     * 삭제 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "is_deleted")
    private YNType isDeleted;
}
