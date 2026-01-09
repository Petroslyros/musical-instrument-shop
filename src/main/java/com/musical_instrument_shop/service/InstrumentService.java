package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.InstrumentInsertDTO;
import com.musical_instrument_shop.dto.InstrumentReadOnlyDTO;
import com.musical_instrument_shop.dto.InstrumentUpdateDTO;
import com.musical_instrument_shop.mapper.Mapper;
import com.musical_instrument_shop.model.Brand;
import com.musical_instrument_shop.model.Category;
import com.musical_instrument_shop.model.Instrument;
import com.musical_instrument_shop.repository.BrandRepository;
import com.musical_instrument_shop.repository.CategoryRepository;
import com.musical_instrument_shop.repository.InstrumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstrumentService implements IInstrumentService{

    private final InstrumentRepository instrumentRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final Mapper mapper;

    @Transactional(rollbackOn = Exception.class)
    public InstrumentReadOnlyDTO saveInstrument(InstrumentInsertDTO dto) throws AppObjectInvalidArgumentException {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Category", "Category with id " + dto.categoryId() + " not found"));

        Brand brand = brandRepository.findById(dto.brandId())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Brand", "Brand with id " + dto.brandId() + " not found"));

        Instrument instrument = mapper.mapToInstrumentEntity(dto, category, brand);
        Instrument savedInstrument = instrumentRepository.save(instrument);
        log.info("Instrument with name={} saved.", dto.name());
        return mapper.mapToInstrumentReadOnlyDTO(savedInstrument);
    }

    public InstrumentReadOnlyDTO getOneInstrument(Long id) throws AppObjectNotFoundException {
        return instrumentRepository.findById(id)
                .map(mapper::mapToInstrumentReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Instrument", "Instrument with id " + id + " not found"));
    }

    public Paginated<InstrumentReadOnlyDTO> getPaginatedInstruments(Pageable pageable) {
        var paginatedInstruments = instrumentRepository.findAll(pageable);
        log.debug("Paginated instruments returned successfully");
        return Paginated.fromPage(paginatedInstruments.map(mapper::mapToInstrumentReadOnlyDTO));
    }

    public Paginated<InstrumentReadOnlyDTO> searchByName(String name, Pageable pageable) {
        var searchResults = instrumentRepository.findByNameContainingIgnoreCase(name, pageable);
        log.debug("Instruments filtered by name={}", name);
        return Paginated.fromPage(searchResults.map(mapper::mapToInstrumentReadOnlyDTO));
    }

    public Paginated<InstrumentReadOnlyDTO> getInstrumentsByCategory(Long categoryId, Pageable pageable) throws AppObjectInvalidArgumentException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppObjectInvalidArgumentException("Category", "Category with id " + categoryId + " not found");
        }

        var instrumentsByCategory = instrumentRepository.findByCategory_Id(categoryId, pageable);
        log.debug("Instruments filtered by category={}", categoryId);
        return Paginated.fromPage(instrumentsByCategory.map(mapper::mapToInstrumentReadOnlyDTO));
    }

    public Paginated<InstrumentReadOnlyDTO> getInstrumentsByBrand(Long brandId, Pageable pageable) throws AppObjectInvalidArgumentException {
        if (!brandRepository.existsById(brandId)) {
            throw new AppObjectInvalidArgumentException("Brand", "Brand with id " + brandId + " not found");
        }

        var instrumentsByBrand = instrumentRepository.findByBrand_Id(brandId, pageable);
        log.debug("Instruments filtered by brand={}", brandId);
        return Paginated.fromPage(instrumentsByBrand.map(mapper::mapToInstrumentReadOnlyDTO));
    }

    @Transactional(rollbackOn = Exception.class)
    public InstrumentReadOnlyDTO updateInstrument(InstrumentUpdateDTO dto) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {
        Instrument existingInstrument = instrumentRepository.findById(dto.id())
                .orElseThrow(() -> new AppObjectNotFoundException("Instrument", "Instrument with id " + dto.id() + " not found"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Category", "Category with id " + dto.categoryId() + " not found"));

        Brand brand = brandRepository.findById(dto.brandId())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Brand", "Brand with id " + dto.brandId() + " not found"));

        Instrument instrument = mapper.mapToInstrumentEntity(dto, category, brand);
        Instrument updatedInstrument = instrumentRepository.save(instrument);
        log.info("Instrument with id={} updated.", dto.id());
        return mapper.mapToInstrumentReadOnlyDTO(updatedInstrument);
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteInstrument(Long id) throws AppObjectNotFoundException {
        Instrument instrument = instrumentRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Instrument", "Instrument with id " + id + " not found"));
        instrumentRepository.delete(instrument);
        log.info("Instrument with id={} deleted.", id);
    }
}