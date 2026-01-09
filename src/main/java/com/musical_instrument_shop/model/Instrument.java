package com.musical_instrument_shop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "instruments", indexes = {
        @Index(name = "idx_instrument_name", columnList = "name"),
        @Index(name = "idx_instrument_price", columnList = "price")
})
public class Instrument extends AbstractEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String name;


    private String description;


    @Column(nullable = false)
    private BigDecimal price;


    @Column(nullable = false)
    private int stock;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;


}
