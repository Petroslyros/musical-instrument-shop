package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.BrandInsertDTO;
import com.musical_instrument_shop.dto.BrandReadOnlyDTO;
import com.musical_instrument_shop.dto.BrandUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface IBrandService {
    BrandReadOnlyDTO saveBrand(BrandInsertDTO dto) throws AppObjectAlreadyExists;
    BrandReadOnlyDTO getOneBrand(Long id) throws AppObjectNotFoundException;
    Paginated<BrandReadOnlyDTO> getPaginatedBrands(Pageable pageable);
    BrandReadOnlyDTO updateBrand(BrandUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExists;
    void deleteBrand(Long id) throws AppObjectNotFoundException;
}