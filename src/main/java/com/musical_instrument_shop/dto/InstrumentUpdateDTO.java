package com.musical_instrument_shop.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InstrumentUpdateDTO(
        @NotNull(message = "Instrument id is required")
        Long id,

        @NotEmpty(message = "Instrument name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,

        @NotNull(message = "Category id is required")
        Long categoryId,

        @NotNull(message = "Brand id is required")
        Long brandId
) {}