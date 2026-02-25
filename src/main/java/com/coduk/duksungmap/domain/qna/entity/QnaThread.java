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
@Table(name = "qna_threads",
        indexes = @Index(name="idx_qna_threads_user_id", columnList="user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE qna_threads SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class QnaThread extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Lob
    @Column(nullable=false)
    private String content;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered = false;

    @Column(name="is_deleted", nullable=false)
    private boolean isDeleted = false;

    public static QnaThread of(User user, String content) {
        QnaThread t = new QnaThread();
        t.user = user;
        t.content = content;
        return t;
    }

    public void markAnswered() {
        this.isAnswered = true;
    }

    public void unmarkAnswered() {
        this.isAnswered = false;
    }

    public void delete() {
        this.isDeleted = true;
    }
}