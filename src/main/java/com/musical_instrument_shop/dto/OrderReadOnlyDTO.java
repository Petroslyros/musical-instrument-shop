package com.musical_instrument_shop.dto;

import com.musical_instrument_shop.core.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OrderReadOnlyDTO(
        Long id,
        Long userId,
        String username,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        Set<OrderItemReadOnlyDTO> items
) {}