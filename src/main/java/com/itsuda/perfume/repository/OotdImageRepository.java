package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.OotdImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OotdImageRepository extends JpaRepository<OotdImage, Long> {

    @Query(value = "SELECT oi FROM OotdImage oi WHERE oi.sequence = 0")
    Page<OotdImage> findByOotdOrderByOotdCreatedAt(Pageable pageable);
}
