package com.itsuda.perfume.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE ootd SET deleted_at = NOW() WHERE id = ?")
public class Ootd extends ModifiableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    @Column(nullable = false)
    private int volume;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int commentCount;

    // ------------------------ 관계 설정 ----------------------------

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<OotdImage> ootdImages = new ArrayList<>();

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<OotdTag> ootdTags = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "FK_OOTD_USER_ID"))
    private User user;

    // ------------------------ 도메인 메서드 ----------------------------

    @Builder
    private Ootd(int likeCount, int volume, String content, User user) {
        this.likeCount = likeCount;
        this.volume = volume;
        this.content = content;
        this.user = user;
    }

    public int increaseLikeCount() {
        return ++this.likeCount;
    }

    public int decreaseLikeCount() {
        return --this.likeCount;
    }
}
