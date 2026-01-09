package com.musical_instrument_shop.mapper;

import com.musical_instrument_shop.dto.*;
import com.musical_instrument_shop.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    // ==================== BRAND ====================

    public BrandReadOnlyDTO mapToBrandReadOnlyDTO(Brand brand) {
        return BrandReadOnlyDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .country(brand.getCountry())
                .build();
    }

    public Brand mapToBrandEntity(BrandInsertDTO dto) {
        Brand brand = new Brand();
        brand.setName(dto.name());
        brand.setCountry(dto.country());
        return brand;
    }

    public Brand mapToBrandEntity(BrandUpdateDTO dto) {
        Brand brand = new Brand();
        brand.setId(dto.id());
        brand.setName(dto.name());
        brand.setCountry(dto.country());
        return brand;
    }

    // ==================== CATEGORY ====================

    public CategoryReadOnlyDTO mapToCategoryReadOnlyDTO(Category category) {
        return CategoryReadOnlyDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category mapToCategoryEntity(CategoryInsertDTO dto) {
        Category category = new Category();
        category.setName(dto.name());
        return category;
    }

    public Category mapToCategoryEntity(CategoryUpdateDTO dto) {
        Category category = new Category();
        category.setId(dto.id());
        category.setName(dto.name());
        return category;
    }

    // ==================== INSTRUMENT ====================

    public InstrumentReadOnlyDTO mapToInstrumentReadOnlyDTO(Instrument instrument) {
        return InstrumentReadOnlyDTO.builder()
                .id(instrument.getId())
                .name(instrument.getName())
                .description(instrument.getDescription())
                .price(instrument.getPrice())
                .stock(instrument.getStock())
                .categoryId(instrument.getCategory().getId())
                .categoryName(instrument.getCategory().getName())
                .brandId(instrument.getBrand().getId())
                .brandName(instrument.getBrand().getName())
                .build();
    }

    public Instrument mapToInstrumentEntity(InstrumentInsertDTO dto, Category category, Brand brand) {
        Instrument instrument = new Instrument();
        instrument.setName(dto.name());
        instrument.setDescription(dto.description());
        instrument.setPrice(dto.price());
        instrument.setStock(dto.stock());
        instrument.setCategory(category);
        instrument.setBrand(brand);
        return instrument;
    }

    public Instrument mapToInstrumentEntity(InstrumentUpdateDTO dto, Category category, Brand brand) {
        Instrument instrument = new Instrument();
        instrument.setId(dto.id());
        instrument.setName(dto.name());
        instrument.setDescription(dto.description());
        instrument.setPrice(dto.price());
        instrument.setStock(dto.stock());
        instrument.setCategory(category);
        instrument.setBrand(brand);
        return instrument;
    }

    // ==================== ORDER ====================

    public OrderReadOnlyDTO mapToOrderReadOnlyDTO(Order order) {
        return OrderReadOnlyDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .username(order.getUser().getUsername())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(this::mapToOrderItemReadOnlyDTO)
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }

    // ==================== ORDER ITEM ====================

    public OrderItemReadOnlyDTO mapToOrderItemReadOnlyDTO(OrderItem orderItem) {
        return OrderItemReadOnlyDTO.builder()
                .id(orderItem.getId())
                .instrumentId(orderItem.getInstrument().getId())
                .instrumentName(orderItem.getInstrument().getName())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .build();
    }

    // ==================== USER ====================

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return UserReadOnlyDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();
    }
}