package com.itsuda.perfume.repository.jdbctemplate;

import com.itsuda.perfume.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostTagJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<Long> tagIds, Post post) {
        jdbcTemplate.batchUpdate("INSERT INTO post_tag (post_id, tag_id) VALUES (?, ?)", tagIds, tagIds.size(), (ps, tagId) -> {
            ps.setLong(1, post.getId());
            ps.setLong(2, tagId);
        });
    }
}
