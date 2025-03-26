package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
