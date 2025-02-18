package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.NoteType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PerfumeAccord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    // ------------------------ 관계 설정 ----------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accord_id")
    private Accord accord;
} 