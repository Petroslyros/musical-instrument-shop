package com.musical_instrument_shop.dto;

import com.musical_instrument_shop.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderUpdateDTO(
        @NotNull(message = "Order id is required")
        Long id,

        @NotNull(message = "Order status is required")
        OrderStatus status
) {}