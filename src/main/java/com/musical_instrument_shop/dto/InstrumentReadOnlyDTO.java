package com.musical_instrument_shop.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InstrumentReadOnlyDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Long categoryId,
        String categoryName,
        Long brandId,
        String brandName
) {}