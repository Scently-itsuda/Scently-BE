package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.PerfumeAccord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerfumeAccordRepository extends JpaRepository<PerfumeAccord, Long> {

    @Query("SELECT pa FROM PerfumeAccord pa " +
           "JOIN FETCH pa.accord " +
           "WHERE pa.perfume = :perfume")
    List<PerfumeAccord> findByPerfume(@Param("perfume") Perfume perfume);
}