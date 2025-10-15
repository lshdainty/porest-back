package com.lshdainty.porest.common.domain;

import com.lshdainty.porest.common.config.database.IpAuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@EntityListeners({AuditingEntityListener.class, IpAuditingEntityListener.class})
public class AuditingFields {
    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "create_by", updatable = false)
    private String createBy;

    @Column(name = "create_ip", updatable = false)
    @Setter // IpAuditingEntityListener에서 사용
    private String createIP;

    @LastModifiedDate
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @LastModifiedBy
    @Column(name = "modify_by")
    private String modifyBy;

    @Column(name = "modify_ip")
    @Setter // IpAuditingEntityListener에서 사용
    private String modifyIP;
}
