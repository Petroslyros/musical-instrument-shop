package com.musical_instrument_shop.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CategoryInsertDTO(
        @NotEmpty(message = "Category name is required")
        String name
) {}