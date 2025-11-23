package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.WishPerfume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishPerfumeRepository extends JpaRepository<WishPerfume, Long> {

    Optional<WishPerfume> findByPerfumeAndCustomer(Perfume perfume, User customer);

}
