package com.musical_instrument_shop.dto;

import lombok.Builder;

@Builder
public record CategoryReadOnlyDTO(
        Long id,
        String name
) {}