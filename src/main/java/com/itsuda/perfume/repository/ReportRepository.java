package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Report;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.ReportTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReportTargetTypeAndTargetId(User reporter, ReportTargetType reportTargetType, Long targetId);
}
