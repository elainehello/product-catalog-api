package com.elaineparra.productcatalog.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;


public record ProductRequest(
        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must not exceed 150 characters")
        String name,

        @Size(max = 1000) String description,

        @NotNull(message = "price is required")
        @Positive(message = "price must be greater than zero")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @NotNull(message = "stock is required")
        @PositiveOrZero(message = "stock must be zero or greater")
        Integer stock
) { }
