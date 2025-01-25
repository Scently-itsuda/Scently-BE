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
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "향수 검색 조건")
@ParameterObject
public class PerfumeRequestDto {
    @Schema(description = "최소 가격", example = "100000", minimum = "0")
    private Integer minPrice;
    
    @Schema(description = "최대 가격", example = "500000", minimum = "0")
    private Integer maxPrice;


    @Schema(description = "성별", example = "FEMALE,UNISEX", examples = {"FEMALE", "UNISEX", "MALE", "UNKNOWN"})
    private List<GenderType> genders;

    @Schema(description = "향 계열", example = "만다린,바닐라", examples = {"만다린", "바닐라", "바이올렛", "베르가못", "..."})
    private List<String> accords;

    @Schema(description = "향의 강도", example = "EDP,EDT", examples = {"EDP", "EDT", "EDC", "PERFUME"})
    private List<PotentialType> potentials;

    @Schema(description = "브랜드", example = "DIOR,CHANEL", examples = {"CHANEL", "DIOR", "GUCCI", "JO MALONE", "CREED", "BYREDO", "..."})
    private List<BrandType> brands;

    @Schema(description = "원산지", example = "FRANCE,ITALY", examples = {"FRANCE", "ITALY", "USA", "..."})
    private List<CountryType> countries;
}
