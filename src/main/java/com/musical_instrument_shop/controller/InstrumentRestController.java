package com.musical_instrument_shop.controller;

import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.exceptions.ValidationException;
import com.musical_instrument_shop.core.filters.GenericFilters;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.InstrumentInsertDTO;
import com.musical_instrument_shop.dto.InstrumentReadOnlyDTO;
import com.musical_instrument_shop.dto.InstrumentUpdateDTO;
import com.musical_instrument_shop.dto.ResponseMessageDTO;
import com.musical_instrument_shop.service.InstrumentService;
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
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
@Tag(name = "Instrument Management")
@SecurityRequirement(name = "Bearer Authentication")
public class InstrumentRestController {

    private final InstrumentService instrumentService;

    @PostMapping
    public ResponseEntity<InstrumentReadOnlyDTO> saveInstrument(
            @Valid @RequestBody InstrumentInsertDTO dto,
            BindingResult bindingResult)
            throws AppObjectInvalidArgumentException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        InstrumentReadOnlyDTO instrumentReadOnlyDTO = instrumentService.saveInstrument(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(instrumentReadOnlyDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(instrumentReadOnlyDTO);
    }

    @GetMapping
    public ResponseEntity<Paginated<InstrumentReadOnlyDTO>> getAllInstruments(
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

        Paginated<InstrumentReadOnlyDTO> instruments = instrumentService.getPaginatedInstruments(filters.getPageable());
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrumentReadOnlyDTO> getInstrumentById(@PathVariable Long id)
            throws AppObjectNotFoundException {
        InstrumentReadOnlyDTO instrument = instrumentService.getOneInstrument(id);
        return ResponseEntity.ok(instrument);
    }

    @GetMapping("/search")
    public ResponseEntity<Paginated<InstrumentReadOnlyDTO>> searchInstruments(
            @RequestParam String name,
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

        Paginated<InstrumentReadOnlyDTO> instruments = instrumentService.searchByName(name, filters.getPageable());
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Paginated<InstrumentReadOnlyDTO>> getInstrumentsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) throws AppObjectInvalidArgumentException {

        GenericFilters filters = new GenericFilters() {};
        filters.setPage(page);
        filters.setPageSize(size);
        filters.setSortBy(sortBy);
        filters.setSortDirection(sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC);

        Paginated<InstrumentReadOnlyDTO> instruments = instrumentService.getInstrumentsByCategory(categoryId, filters.getPageable());
        return ResponseEntity.ok(instruments);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Paginated<InstrumentReadOnlyDTO>> getInstrumentsByBrand(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) throws AppObjectInvalidArgumentException {

        GenericFilters filters = new GenericFilters() {};
        filters.setPage(page);
        filters.setPageSize(size);
        filters.setSortBy(sortBy);
        filters.setSortDirection(sortDirection.equalsIgnoreCase("DESC") ?
                Sort.Direction.DESC : Sort.Direction.ASC);

        Paginated<InstrumentReadOnlyDTO> instruments = instrumentService.getInstrumentsByBrand(brandId, filters.getPageable());
        return ResponseEntity.ok(instruments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstrumentReadOnlyDTO> updateInstrument(
            @PathVariable Long id,
            @Valid @RequestBody InstrumentUpdateDTO dto,
            BindingResult bindingResult)
            throws AppObjectNotFoundException, AppObjectInvalidArgumentException, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        InstrumentReadOnlyDTO instrumentReadOnlyDTO = instrumentService.updateInstrument(dto);
        return ResponseEntity.ok(instrumentReadOnlyDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteInstrument(@PathVariable Long id)
            throws AppObjectNotFoundException {
        instrumentService.deleteInstrument(id);
        return ResponseEntity.ok(new ResponseMessageDTO("Success", "Instrument deleted successfully"));
    }
}