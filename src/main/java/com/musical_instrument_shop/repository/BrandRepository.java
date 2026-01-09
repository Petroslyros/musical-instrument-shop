package com.musical_instrument_shop.repository;

import com.musical_instrument_shop.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>,
        JpaSpecificationExecutor<Brand> {

    Optional<Brand> findByName(String name);

    boolean existsByName(String name);
}
