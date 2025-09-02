package com.itsuda.perfume.repository.jdbctemplate;

import com.itsuda.perfume.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<Tag> tags) {
        jdbcTemplate.batchUpdate("INSERT INTO tag (name) VALUES (?)", tags, tags.size(), (ps, tag) -> ps.setString(1, tag.getName()));
    }

    @Transactional
    public Long getLastInsertedId() {
        return jdbcTemplate.queryForObject("SELECT last_insert_id()", Long.class);
    }
}
