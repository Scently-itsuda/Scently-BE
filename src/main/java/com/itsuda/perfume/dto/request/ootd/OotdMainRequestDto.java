package com.itsuda.perfume.dto.request.ootd;

import com.itsuda.perfume.domain.type.OotdOrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "OOTD 게시글 조회 ")
public class OotdMainRequestDto {
    private OotdOrderType order;
    private int page;
    private int size;
}
