package com.musical_instrument_shop.dto;

import lombok.Builder;

@Builder
public record BrandReadOnlyDTO(
        Long id,
        String name,
        String country
) {}