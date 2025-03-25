package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
