package com.itsuda.perfume.dto.request.ootd;

import com.itsuda.perfume.domain.type.OotdOrderType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OotdMainRequestDto {
    private OotdOrderType order;
    private int page;
    private int size;
}
