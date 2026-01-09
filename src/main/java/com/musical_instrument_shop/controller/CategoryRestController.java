package com.musical_instrument_shop.controller;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.exceptions.ValidationException;
import com.musical_instrument_shop.core.filters.GenericFilters;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.CategoryInsertDTO;
import com.musical_instrument_shop.dto.CategoryReadOnlyDTO;
import com.musical_instrument_shop.dto.CategoryUpdateDTO;
import com.musical_instrument_shop.dto.ResponseMessageDTO;
import com.musical_instrument_shop.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryReadOnlyDTO> saveCategory(
            @Valid @RequestBody CategoryInsertDTO dto,
            BindingResult bindingResult)
            throws AppObjectAlreadyExists, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        CategoryReadOnlyDTO categoryReadOnlyDTO = categoryService.saveCategory(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryReadOnlyDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(categoryReadOnlyDTO);
    }

    @GetMapping
    public ResponseEntity<Paginated<CategoryReadOnlyDTO>> getAllCategories(
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

        Paginated<CategoryReadOnlyDTO> categories = categoryService.getPaginatedCategories(filters.getPageable());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryReadOnlyDTO> getCategoryById(@PathVariable Long id)
            throws AppObjectNotFoundException {
        CategoryReadOnlyDTO category = categoryService.getOneCategory(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryReadOnlyDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO dto,
            BindingResult bindingResult)
            throws AppObjectNotFoundException, AppObjectAlreadyExists, ValidationException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        CategoryReadOnlyDTO categoryReadOnlyDTO = categoryService.updateCategory(dto);
        return ResponseEntity.ok(categoryReadOnlyDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteCategory(@PathVariable Long id)
            throws AppObjectNotFoundException {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ResponseMessageDTO("Success", "Category deleted successfully"));
    }
}