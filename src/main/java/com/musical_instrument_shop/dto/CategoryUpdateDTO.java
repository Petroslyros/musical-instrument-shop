package com.musical_instrument_shop.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CategoryUpdateDTO(
        @NotNull(message = "Category id is required")
        Long id,

        @NotEmpty(message = "Category name is required")
        String name
) {}