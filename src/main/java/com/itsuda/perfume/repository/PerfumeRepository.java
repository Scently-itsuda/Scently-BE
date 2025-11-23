package com.itsuda.perfume.repository;

import com.itsuda.perfume.domain.Perfume;
import com.itsuda.perfume.domain.User;
import com.itsuda.perfume.domain.type.BrandType;
import com.itsuda.perfume.domain.type.CountryType;
import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.domain.type.PotentialType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, Long> {

    // 향수 목록 조회
    // 프로젝트가 확장되거나 검색 옵션이 바뀌는 경우 QueryDSL로 전환하는게 좋을 듯?
    @Query("SELECT DISTINCT p FROM Perfume p " +
            "INNER JOIN p.perfumeVolumes pv " +
            "LEFT JOIN p.perfumeAccords pa " +
            "LEFT JOIN pa.accord a " +
            "WHERE " +
            "(:minPrice IS NULL OR pv.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR pv.price <= :maxPrice) AND " +
            "(:genders IS NULL OR p.gender IN :genders) AND " +
            "(:accords IS NULL OR a.name IN :accords) AND " +
            "(:potencies IS NULL OR p.potential IN :potencies) AND " +
            "(:brands IS NULL OR p.brand IN :brands) AND " +
            "(:countries IS NULL OR p.country IN :countries)")
    List<Perfume> findBySearchOptions(
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("genders") List<GenderType> genders,
            @Param("accords") List<String> accords,
            @Param("potencies") List<PotentialType> potencies,
            @Param("brands") List<BrandType> brands,
            @Param("countries") List<CountryType> countries
    );

    List<Perfume> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT DISTINCT p FROM Perfume p " +
            "INNER JOIN WishPerfume wp ON p = wp.perfume AND wp.customer = :user " +
            "LEFT JOIN p.perfumeVolumes pv " +
            "LEFT JOIN p.perfumeAccords pa " +
            "LEFT JOIN pa.accord a " +
            "WHERE (:minPrice IS NULL OR pv.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR pv.price <= :maxPrice) AND " +
            "(:genders IS NULL OR p.gender IN :genders) AND " +
            "(:accords IS NULL OR a.name IN :accords) AND " +
            "(:potencies IS NULL OR p.potential IN :potencies) AND " +
            "(:brands IS NULL OR p.brand IN :brands) AND " +
            "(:countries IS NULL OR p.country IN :countries)")
    Page<Perfume> findAllWishPerfumeBySearchOptions(
            Pageable pageable,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("genders") List<GenderType> genders,
            @Param("accords") List<String> accords,
            @Param("potencies") List<PotentialType> potencies,
            @Param("brands") List<BrandType> brands,
            @Param("countries") List<CountryType> countries,
            @Param("user") User user
    );
}
