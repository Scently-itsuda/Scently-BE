package com.itsuda.perfume.dto.request.report;

import com.itsuda.perfume.domain.type.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReviewReportDto(
        @NotNull(message = "NOT_EXIST_REPORT_TYPE")
        @Schema(description = "신고 사유", example = "스팸, 광고")
        ReportType reportType,
        @Schema(description = "기타 사유", example = "이거 계속 중복되는 자유게시글이네요.", nullable = true)
        String otherReason
) {
}
