package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import com.itsuda.perfume.domain.OotdPerfume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OotdPerfumeRepository extends JpaRepository<OotdPerfume, Long> {

    List<OotdPerfume> findByOotd(Ootd otd);
}
