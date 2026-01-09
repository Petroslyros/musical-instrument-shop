package com.musical_instrument_shop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record BrandInsertDTO(
        @NotEmpty(message = "Brand name is required")
        String name,

        String country
) {}