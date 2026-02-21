package com.coduk.duksungmap.domain.qna.entity;

import com.coduk.duksungmap.domain.user.entity.User;
import com.coduk.duksungmap.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name="qna_messages",
        indexes = {
                @Index(name="idx_qna_messages_thread_id", columnList="thread_id"),
                @Index(name="idx_qna_messages_user_id", columnList="user_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE qna_threads SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class QnaMessage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="thread_id", nullable=false)
    private QnaThread thread;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Lob
    @Column(nullable=false)
    private String content;

    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted = false;

    public static QnaMessage of(QnaThread thread, User user, String content) {
        QnaMessage m = new QnaMessage();
        m.thread = thread;
        m.user = user;
        m.content = content;
        return m;
    }

    public void delete() {
        this.isDeleted = true;
    }
}