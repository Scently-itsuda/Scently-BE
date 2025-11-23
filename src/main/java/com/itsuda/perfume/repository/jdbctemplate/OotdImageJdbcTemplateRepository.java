package com.itsuda.perfume.repository.jdbctemplate;

import com.itsuda.perfume.domain.OotdImage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OotdImageJdbcTemplateRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void batchInsert(List<OotdImage> ootdImages) {
        jdbcTemplate.batchUpdate("INSERT INTO ootd_image (origin_name, save_name, sequence, ootd_id, created_at) " +
                "VALUES (?, ?, ?, ?, ?)", ootdImages, ootdImages.size(), (ps, ootdImage) -> {
            ps.setString(1, ootdImage.getOriginName());
            ps.setString(2, ootdImage.getSaveName());
            ps.setInt(3, ootdImage.getSequence());
            ps.setLong(4, ootdImage.getOotd().getId());
            ps.setDate(5, Date.valueOf(LocalDate.now()));
        });
    }
}