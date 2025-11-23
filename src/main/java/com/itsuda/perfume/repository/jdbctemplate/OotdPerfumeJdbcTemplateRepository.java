package com.itsuda.perfume.repository.jdbctemplate;

import com.itsuda.perfume.domain.OotdPerfume;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OotdPerfumeJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<OotdPerfume> ootdPerfumes) {
        jdbcTemplate.batchUpdate("INSERT INTO ootd_perfume (ootd_id, perfume_id) " +
                "VALUES (?, ?)", ootdPerfumes, ootdPerfumes.size(), (ps, ootdPerfume) -> {
            ps.setLong(1, ootdPerfume.getOotd().getId());
            ps.setLong(2, ootdPerfume.getPerfume().getId());
        });
    }
}
