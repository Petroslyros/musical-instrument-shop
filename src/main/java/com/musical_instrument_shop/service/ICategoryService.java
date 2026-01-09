package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.CategoryInsertDTO;
import com.musical_instrument_shop.dto.CategoryReadOnlyDTO;
import com.musical_instrument_shop.dto.CategoryUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    CategoryReadOnlyDTO saveCategory(CategoryInsertDTO dto) throws AppObjectAlreadyExists;
    CategoryReadOnlyDTO getOneCategory(Long id) throws AppObjectNotFoundException;
    Paginated<CategoryReadOnlyDTO> getPaginatedCategories(Pageable pageable);
    CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExists;
    void deleteCategory(Long id) throws AppObjectNotFoundException;
}