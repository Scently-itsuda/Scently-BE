package com.itsuda.perfume.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ootd extends ModifiableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int likeCount;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int commentCount;

    @Column(nullable = false)
    private int volume;

    @Column(nullable = false)
    private String content;

    // ------------------------ 관계 설정 ----------------------------

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<OotdImage> ootdImages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "perfume_id", nullable = false)
    private Perfume perfume;

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<OotdTag> ootdTags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "ootd", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    private Ootd(int likeCount, int commentCount, int volume, String content, Perfume perfume, User user) {
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.volume = volume;
        this.content = content;
        this.perfume = perfume;
        this.user = user;
    }

    public int increaseLikeCount() {
        return ++this.likeCount;
    }

    public int decreaseLikeCount() {
        return --this.likeCount;
    }
}
