package com.musical_instrument_shop.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemReadOnlyDTO(
        Long id,
        Long instrumentId,
        String instrumentName,
        Integer quantity,
        BigDecimal priceAtPurchase
) {}