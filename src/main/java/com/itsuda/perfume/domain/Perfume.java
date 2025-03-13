package com.itsuda.perfume.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.PotentialType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Perfume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    private String imageUri;
    
    @Enumerated(EnumType.STRING)
    private GenderType gender;
    
    @Enumerated(EnumType.STRING)
    private BrandType brand;
    
    @Enumerated(EnumType.STRING)
    private CountryType country;
    
    @Enumerated(EnumType.STRING)
    private PotentialType potential;
    
    private String description; // 향 한줄 소개
    
    @Column(name = "registered_at")
    private LocalDate registeredAt;

    // ------------------------ 관계 설정 ----------------------------
    
    @OneToMany(mappedBy = "perfume")
    private List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "perfume")
    private List<WishPerfume> wishPerfumes = new ArrayList<>();
    
    @OneToMany(mappedBy = "perfume")
    private List<PerfumeAccord> perfumeAccords = new ArrayList<>();
    
    @OneToMany(mappedBy = "perfume")
    private List<PerfumeVolume> perfumeVolumes = new ArrayList<>();
    
    @OneToMany(mappedBy = "perfume")
    private List<PerfumeDetail> details = new ArrayList<>();

    @Builder
    private Perfume(String name, String imageUri, GenderType gender, BrandType brand, CountryType country,
                   PotentialType potential, String description, LocalDate registeredAt) {
        this.name = name;
        this.imageUri = imageUri;
        this.gender = gender;
        this.brand = brand;
        this.country = country;
        this.potential = potential;
        this.description = description;
        this.registeredAt = registeredAt;
    }
}

