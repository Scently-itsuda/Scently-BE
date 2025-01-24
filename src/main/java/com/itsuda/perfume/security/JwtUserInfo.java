package com.itsuda.perfume.security;

import com.itsuda.perfume.domain.type.ERole;

public record JwtUserInfo(Long id, ERole role) {
}
