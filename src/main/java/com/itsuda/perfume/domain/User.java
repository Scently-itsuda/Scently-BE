package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.domain.type.ERole;
import com.itsuda.perfume.domain.type.GenderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
    public User(String email, GenderType gender, String imageUrl, String nickname, String presentation,
                EProvider provider, ERole role, String serialId, String username) {
        this.email = email;
        this.gender = gender;
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

    public void register(GenderType gender, String birthDate, String nickname) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        this.role = ERole.USER;
    }

    public int getAge(LocalDate time) {
        LocalDate userLocalDate = LocalDate.parse(birthDate);
        return Math.abs((int)ChronoUnit.YEARS.between(userLocalDate, time));
    }

    public void updateBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
} 