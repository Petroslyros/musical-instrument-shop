package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectAlreadyExists;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.CategoryInsertDTO;
import com.musical_instrument_shop.dto.CategoryReadOnlyDTO;
import com.musical_instrument_shop.dto.CategoryUpdateDTO;
import com.musical_instrument_shop.mapper.Mapper;
import com.musical_instrument_shop.model.Category;
import com.musical_instrument_shop.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Transactional(rollbackOn = Exception.class)
    public CategoryReadOnlyDTO saveCategory(CategoryInsertDTO dto) throws AppObjectAlreadyExists {
        if (categoryRepository.existsByName(dto.name())) {
            throw new AppObjectAlreadyExists("Category", "Category with name " + dto.name() + " already exists");
        }

        Category category = mapper.mapToCategoryEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category with name={} saved.", dto.name());
        return mapper.mapToCategoryReadOnlyDTO(savedCategory);
    }

    public CategoryReadOnlyDTO getOneCategory(Long id) throws AppObjectNotFoundException {
        return categoryRepository.findById(id)
                .map(mapper::mapToCategoryReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Category", "Category with id " + id + " not found"));
    }

    public Paginated<CategoryReadOnlyDTO> getPaginatedCategories(Pageable pageable) {
        var paginatedCategories = categoryRepository.findAll(pageable);
        log.debug("Paginated categories returned successfully");
        return Paginated.fromPage(paginatedCategories.map(mapper::mapToCategoryReadOnlyDTO));
    }

    @Transactional(rollbackOn = Exception.class)
    public CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExists {
        Category existingCategory = categoryRepository.findById(dto.id())
                .orElseThrow(() -> new AppObjectNotFoundException("Category", "Category with id " + dto.id() + " not found"));

        if (!existingCategory.getName().equals(dto.name()) && categoryRepository.existsByName(dto.name())) {
            throw new AppObjectAlreadyExists("Category", "Category with name " + dto.name() + " already exists");
        }

        Category category = mapper.mapToCategoryEntity(dto);
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category with id={} updated.", dto.id());
        return mapper.mapToCategoryReadOnlyDTO(updatedCategory);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteCategory(Long id) throws AppObjectNotFoundException {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Category", "Category with id " + id + " not found"));
        categoryRepository.delete(category);
        log.info("Category with id={} deleted.", id);
    }
}