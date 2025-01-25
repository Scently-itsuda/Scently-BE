package com.itsuda.perfume.dto.request;

import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfumeRequestDto {
    @Schema(description = "가격", example = "150000", minimum = "0")
    private Integer price;

    @Schema(description = "성별", example = "UNKNOWN", allowableValues = {"MALE", "FEMALE", "UNISEX", "UNKNOWN"})
    private GenderType gender;

    @Schema(description = "향 계열", example = "만다린", examples = {"만다린", "바닐라", "바이올렛", "베르가못", "..."})
    private String accord;

    @Schema(description = "향의 강도", example = "EDT", allowableValues = {"EDP", "EDT", "EDC", "PERFUME"})
    private PotentialType potential;

    @Schema(description = "브랜드", example = "CHANEL", examples = {"CHANEL", "DIOR", "GUCCI", "JO MALONE", "CREED", "BYREDO", "..."})
    private BrandType brand;

    @Schema(description = "원산지", example = "FRANCE", examples = {"FRANCE", "ITALY", "USA", "..."})
    private CountryType country;
}
