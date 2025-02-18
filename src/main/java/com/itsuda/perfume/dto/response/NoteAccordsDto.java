package com.itsuda.perfume.dto.response;

import com.itsuda.perfume.domain.PerfumeAccord;
import java.util.ArrayList;
import java.util.List;

public record NoteAccordsDto(
    List<AccordDto> topNotes,
    List<AccordDto> middleNotes,
    List<AccordDto> baseNotes,
    List<AccordDto> unknownNotes
) {
    public static NoteAccordsDto from(List<PerfumeAccord> perfumeAccords) {
        List<AccordDto> tops = new ArrayList<>();
        List<AccordDto> middles = new ArrayList<>();
        List<AccordDto> bases = new ArrayList<>();
        List<AccordDto> unknowns = new ArrayList<>();

        for (PerfumeAccord pa : perfumeAccords) {
            AccordDto accordDto = AccordDto.from(pa.getAccord());
            switch (pa.getNoteType()) {
                case TOP -> tops.add(accordDto);
                case MIDDLE -> middles.add(accordDto);
                case BASE -> bases.add(accordDto);
                case UNKNOWN -> unknowns.add(accordDto);
            }
        }

        return new NoteAccordsDto(tops, middles, bases, unknowns);
    }
} 