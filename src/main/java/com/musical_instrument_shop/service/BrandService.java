package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.BrandInsertDTO;
import com.musical_instrument_shop.dto.BrandReadOnlyDTO;
import com.musical_instrument_shop.dto.BrandUpdateDTO;
import com.musical_instrument_shop.mapper.Mapper;
import com.musical_instrument_shop.model.Brand;
import com.musical_instrument_shop.repository.BrandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BrandService implements IBrandService {

    private final BrandRepository brandRepository;
    private final Mapper mapper;

    @Transactional(rollbackOn = Exception.class)
    public BrandReadOnlyDTO saveBrand(BrandInsertDTO dto) throws AppObjectAlreadyExists {
        if (brandRepository.existsByName(dto.name())) {
            throw new AppObjectAlreadyExists("Brand", "Brand with name " + dto.name() + " already exists");
        }

        Brand brand = mapper.mapToBrandEntity(dto);
        Brand savedBrand = brandRepository.save(brand);
        log.info("Brand with name={} saved.", dto.name());
        return mapper.mapToBrandReadOnlyDTO(savedBrand);
    }

    public BrandReadOnlyDTO getOneBrand(Long id) throws AppObjectNotFoundException {
        return brandRepository.findById(id)
                .map(mapper::mapToBrandReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Brand", "Brand with id " + id + " not found"));
    }

    public Paginated<BrandReadOnlyDTO> getPaginatedBrands(Pageable pageable) {
        var paginatedBrands = brandRepository.findAll(pageable);
        log.debug("Paginated brands returned successfully");
        return Paginated.fromPage(paginatedBrands.map(mapper::mapToBrandReadOnlyDTO));
    }

    @Transactional(rollbackOn = Exception.class)
    public BrandReadOnlyDTO updateBrand(BrandUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExists {
        Brand existingBrand = brandRepository.findById(dto.id())
                .orElseThrow(() -> new AppObjectNotFoundException("Brand", "Brand with id " + dto.id() + " not found"));

        if (!existingBrand.getName().equals(dto.name()) && brandRepository.existsByName(dto.name())) {
            throw new AppObjectAlreadyExists("Brand", "Brand with name " + dto.name() + " already exists");
        }

        Brand brand = mapper.mapToBrandEntity(dto);
        Brand updatedBrand = brandRepository.save(brand);
        log.info("Brand with id={} updated.", dto.id());
        return mapper.mapToBrandReadOnlyDTO(updatedBrand);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteBrand(Long id) throws AppObjectNotFoundException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Brand", "Brand with id " + id + " not found"));
        brandRepository.delete(brand);
        log.info("Brand with id={} deleted.", id);
    }
}