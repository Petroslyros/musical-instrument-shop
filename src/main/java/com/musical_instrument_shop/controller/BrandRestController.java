package com.musical_instrument_shop.controller;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.exceptions.ValidationException;
import com.musical_instrument_shop.core.filters.GenericFilters;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.BrandInsertDTO;
import com.musical_instrument_shop.dto.BrandReadOnlyDTO;
import com.musical_instrument_shop.dto.BrandUpdateDTO;
import com.musical_instrument_shop.dto.ResponseMessageDTO;
import com.musical_instrument_shop.service.BrandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Management")
@SecurityRequirement(name = "Bearer Authentication")
public class BrandRestController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<BrandReadOnlyDTO> saveBrand(
            @Valid @RequestBody BrandInsertDTO dto,
            BindingResult bindingResult)
            throws AppObjectAlreadyExists, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        BrandReadOnlyDTO brandReadOnlyDTO = brandService.saveBrand(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(brandReadOnlyDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(brandReadOnlyDTO);
    }

    @GetMapping
    public ResponseEntity<Paginated<BrandReadOnlyDTO>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        GenericFilters filters = new GenericFilters() {};
        filters.setPage(page);
        filters.setPageSize(size);
        filters.setSortBy(sortBy);
        filters.setSortDirection(sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC);

        Paginated<BrandReadOnlyDTO> brands = brandService.getPaginatedBrands(filters.getPageable());
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandReadOnlyDTO> getBrandById(@PathVariable Long id)
            throws AppObjectNotFoundException {
        BrandReadOnlyDTO brand = brandService.getOneBrand(id);
        return ResponseEntity.ok(brand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandReadOnlyDTO> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandUpdateDTO dto,
            BindingResult bindingResult)
            throws AppObjectNotFoundException, AppObjectAlreadyExists, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        BrandReadOnlyDTO brandReadOnlyDTO = brandService.updateBrand(dto);
        return ResponseEntity.ok(brandReadOnlyDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteBrand(@PathVariable Long id)
            throws AppObjectNotFoundException {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(new ResponseMessageDTO("Success", "Brand deleted successfully"));
    }
}