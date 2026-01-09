package com.musical_instrument_shop.dto;

import com.musical_instrument_shop.core.enums.Role;
import lombok.Builder;

@Builder
public record UserReadOnlyDTO(
        Long id,
        String username,
        String email,
        String firstname,
        String lastname,
        Role role
) {}