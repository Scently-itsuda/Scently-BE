package com.itsuda.perfume.repository.jdbctemplate;

import com.itsuda.perfume.domain.Ootd;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OotdTagJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<Long> tagIds, Ootd ootd) {
        jdbcTemplate.batchUpdate("INSERT INTO ootd_tag (ootd_id, tag_id) VALUES (?, ?)", tagIds, tagIds.size(), (ps, tagId) -> {
            ps.setLong(1, ootd.getId());
            ps.setLong(2, tagId);
        });
    }
}
