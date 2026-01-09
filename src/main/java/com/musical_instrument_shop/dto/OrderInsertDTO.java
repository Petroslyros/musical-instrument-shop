package com.musical_instrument_shop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record OrderInsertDTO(
        @NotNull(message = "User id is required")
        Long userId,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        Set<OrderItemInsertDTO> items
) {}