package com.itsuda.perfume.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.itsuda.perfume.domain.type.GenderType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    
    private int score;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @Column(name = "perfume_gender")
    @Enumerated(EnumType.STRING)
    private GenderType perfumeGender;
    
    @Column(name = "potential_score")
    private int potentialScore;
    
    private int weight;

    // ------------------------ 관계 설정 ----------------------------
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;

    @Builder
    private Review(String content, int score, LocalDateTime createdAt, LocalDateTime modifiedAt, GenderType perfumeGender, int potentialScore, int weight, User user, Perfume perfume) {
        this.content = content;
        this.score = score;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.perfumeGender = perfumeGender;
        this.potentialScore = potentialScore;
        this.weight = weight;
        this.user = user;
        this.perfume = perfume;
    }
}