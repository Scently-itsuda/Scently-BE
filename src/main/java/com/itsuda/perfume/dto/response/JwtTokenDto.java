package com.itsuda.perfume.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtTokenDto {
    @NotBlank
    @NotEmpty
    String accessToken;
    @NotBlank
    @NotEmpty
    String refreshToken;

    @Builder
    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
