package com.musical_instrument_shop.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BrandUpdateDTO(
        @NotNull(message = "Brand id is required")
        Long id,

        @NotEmpty(message = "Brand name is required")
        String name,

        String country
) {}