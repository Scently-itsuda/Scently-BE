package com.itsuda.perfume.domain;

import com.itsuda.perfume.domain.type.ReportTargetType;
import com.itsuda.perfume.domain.type.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReportTargetType targetType;

    @Column(nullable = false)
    private ReportType reportType;

    private String otherReason;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User reportUser;

    @Builder
    private Report(Long targetId, ReportTargetType targetType, ReportType reportType, String otherReason, User reportUser) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.reportType = reportType;
        this.otherReason = otherReason;
        this.reportUser = reportUser;
    }
}
