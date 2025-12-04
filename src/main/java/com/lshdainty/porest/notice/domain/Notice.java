package com.lshdainty.porest.notice.domain;

import com.lshdainty.porest.common.domain.AuditingFields;
import com.lshdainty.porest.common.type.YNType;
import com.lshdainty.porest.notice.type.NoticeType;
import com.lshdainty.porest.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice")
public class Notice extends AuditingFields {
    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private User writer;

    @Column(name = "notice_title", nullable = false)
    private String title;

    @Lob
    @Column(name = "notice_content", nullable = false)
    private String content;

    @Column(name = "notice_type")
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @Column(name = "is_pinned")
    @Enumerated(EnumType.STRING)
    private YNType isPinned;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_deleted")
    @Enumerated(EnumType.STRING)
    private YNType isDeleted;

    /**
     * 공지사항 생성 함수
     *
     * @return Notice
     */
    public static Notice createNotice(User writer, String title, String content, NoticeType type,
                                       YNType isPinned, LocalDate startDate, LocalDate endDate) {
        Notice notice = new Notice();
        notice.writer = writer;
        notice.title = title;
        notice.content = content;
        notice.type = type;
        notice.isPinned = isPinned;
        notice.viewCount = 0L;
        notice.startDate = startDate;
        notice.endDate = endDate;
        notice.isDeleted = YNType.N;
        return notice;
    }

    /**
     * 공지사항 수정 함수
     */
    public void updateNotice(String title, String content, NoticeType type,
                              YNType isPinned, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.isPinned = isPinned;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 공지사항 삭제 함수 (소프트 딜리트)
     */
    public void deleteNotice() {
        this.isDeleted = YNType.Y;
    }

    /**
     * 조회수 증가 함수
     */
    public void increaseViewCount() {
        this.viewCount++;
    }
}
