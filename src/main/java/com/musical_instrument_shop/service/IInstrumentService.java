package com.musical_instrument_shop.service;

import com.musical_instrument_shop.core.exceptions.AppObjectInvalidArgumentException;
import com.musical_instrument_shop.core.exceptions.AppObjectNotFoundException;
import com.musical_instrument_shop.core.filters.Paginated;
import com.musical_instrument_shop.dto.InstrumentInsertDTO;
import com.musical_instrument_shop.dto.InstrumentReadOnlyDTO;
import com.musical_instrument_shop.dto.InstrumentUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface IInstrumentService {
    InstrumentReadOnlyDTO saveInstrument(InstrumentInsertDTO dto) throws AppObjectInvalidArgumentException;
    InstrumentReadOnlyDTO getOneInstrument(Long id) throws AppObjectNotFoundException;
    Paginated<InstrumentReadOnlyDTO> getPaginatedInstruments(Pageable pageable);
    Paginated<InstrumentReadOnlyDTO> searchByName(String name, Pageable pageable);
    Paginated<InstrumentReadOnlyDTO> getInstrumentsByCategory(Long categoryId, Pageable pageable) throws AppObjectInvalidArgumentException;
    Paginated<InstrumentReadOnlyDTO> getInstrumentsByBrand(Long brandId, Pageable pageable) throws AppObjectInvalidArgumentException;
    InstrumentReadOnlyDTO updateInstrument(InstrumentUpdateDTO dto) throws AppObjectNotFoundException, AppObjectInvalidArgumentException;
    void deleteInstrument(Long id) throws AppObjectNotFoundException;
}