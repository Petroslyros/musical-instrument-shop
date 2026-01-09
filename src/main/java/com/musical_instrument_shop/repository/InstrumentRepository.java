package com.musical_instrument_shop.repository;

import com.musical_instrument_shop.model.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument, Long>,
        JpaSpecificationExecutor<Instrument> {

    Page<Instrument> findByCategory_Id(Long categoryId, Pageable pageable);

    Page<Instrument> findByBrand_Id(Long brandId, Pageable pageable);

    Page<Instrument> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Instrument> findByStockLessThan(int stock);
}