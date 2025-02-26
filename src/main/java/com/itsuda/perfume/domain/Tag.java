package com.itsuda.perfume.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // ------------------------ 관계 설정 ----------------------------

    @OneToMany(mappedBy = "tag")
    private List<OotdTag> ootdTags;

    @Builder
    private Tag(String name, List<OotdTag> ootdTags) {
        this.name = name;
        this.ootdTags = ootdTags;
    }
}
