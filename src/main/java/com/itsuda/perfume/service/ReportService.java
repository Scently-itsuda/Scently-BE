package com.itsuda.perfume.service;

import com.itsuda.perfume.domain.Report;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.ReportTargetType;
import com.itsuda.perfume.dto.request.report.OotdReportDto;
import com.itsuda.perfume.dto.response.report.ReportedOotdDto;
import com.itsuda.perfume.exception.RestApiException;
import com.itsuda.perfume.repository.OotdRepository;
import com.itsuda.perfume.repository.ReportRepository;
import com.itsuda.perfume.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.itsuda.perfume.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final OotdRepository ootdRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public ReportedOotdDto reportOotdByOotdIdAndUserId(OotdReportDto ootdReportDto, Long ootdId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestApiException(NOT_FOUND_OOTD));
        if (!ootdRepository.existsById(ootdId)) {
            throw new RestApiException(NOT_FOUND_OOTD);
        }
        if (reportRepository.existsByReporterAndReportTargetTypeAndTargetId(user, ReportTargetType.OOTD, ootdId)) {
            throw new RestApiException(ALREADY_REPORTED_OOTD);
        }

        Report report = reportRepository.save(Report.builder().reporter(user)
                .reportType(ootdReportDto.reportType())
                .otherReason(ootdReportDto.otherReason())
                .reportTargetType(ReportTargetType.OOTD)
                .targetId(ootdId)
                .otherReason(ootdReportDto.otherReason()).build());
        return new ReportedOotdDto(report.getId());
    }
}
