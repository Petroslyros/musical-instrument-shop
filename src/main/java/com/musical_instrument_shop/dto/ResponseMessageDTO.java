package com.musical_instrument_shop.dto;

public record ResponseMessageDTO(String code, String description) {

    public ResponseMessageDTO(String code) {
        this(code, "");     // Calls the canonical constructor
    }
}
