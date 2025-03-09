package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Ootd;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OotdRepository extends JpaRepository<Ootd, Long> {

    @Override
    @EntityGraph(attributePaths = {"ootdImages", "ootdTags"})
    Optional<Ootd> findById(Long id);
}
