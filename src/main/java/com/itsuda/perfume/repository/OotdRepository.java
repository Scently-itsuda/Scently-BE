package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OotdRepository extends JpaRepository<Ootd, Long> {
}
