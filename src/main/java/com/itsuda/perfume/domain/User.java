package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") 
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_id", nullable = false, unique = true)
    private String serialId;
    
    @Column(unique = true)
    private String nickname;

    @Column(name = "birth_date")
    private String birthDate;
    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProvider provider;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Column(name = "image_url")
    private String imageUrl;
    
    private String username;
    
    @Column(unique = true)
    private String email;
    
    private String presentation;
    
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(name = "refresh_token")
    private String refreshToken;

    // ------------------------ 관계 설정 ----------------------------
    
    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "customer")
    private List<WishPerfume> wishPerfumes = new ArrayList<>();


    // ------------------------ 생성자 ----------------------------


    @Builder
    public User(String email, GenderType gender, Long id, String imageUrl, String nickname, String presentation,
                EProvider provider, ERole role, String serialId, String username) {
        this.email = email;
        this.gender = gender;
        this.id = id;
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.presentation = presentation;
        this.provider = provider;
        this.role = role;
        this.serialId = serialId;
        this.username = username;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void register(String nickname) {
        this.nickname = nickname;
        this.role = ERole.USER;
    }

    public void updateUserInfo(GenderType gender, String birthDate, String nickname) {
        if (nickname != null && (!Objects.equals(this.nickname, nickname))) {
            this.nickname = nickname;
        }
        if (gender != null && gender != this.gender) {
            this.gender = gender;
        }
        if (birthDate != null && !this.birthDate.equals(birthDate)) {
            this.birthDate = birthDate;
        }
    }
} 