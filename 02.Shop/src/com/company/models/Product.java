package com.company.models;

import com.company.exceptions.ExpiredProductException;
import com.company.helperClasses.ProductCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;

public class Product implements Serializable {

    private int id;
    private String name;
    private BigDecimal initialPrice;
    private ProductCategory category;
    private LocalDate expiryDate;

    public Product(int id, String name, BigDecimal initialPrice, ProductCategory category, LocalDate expiryDate) {
        this.id = id;
        this.name = name;
        this.initialPrice = initialPrice;
        this.category = category;
        this.expiryDate = expiryDate;
    }

    public BigDecimal sellPrice(Map<ProductCategory, Double> percentsByCategory, Entry<Integer, Double> expiryDiscounts) throws ExpiredProductException {
        if (this.daysBeforeExpiry() <= 0) throw new ExpiredProductException(this.daysBeforeExpiry() * -1);
        BigDecimal calculatedPrice = initialPrice;
        BigDecimal additional = initialPrice.multiply(BigDecimal.valueOf(percentsByCategory.get(this.category)));
        calculatedPrice = calculatedPrice.add(additional);

        if (this.daysBeforeExpiry() <= expiryDiscounts.getKey()) {
            BigDecimal discount = calculatedPrice.multiply(BigDecimal.valueOf(expiryDiscounts.getValue()));
            calculatedPrice = calculatedPrice.subtract(discount);
        }

        return calculatedPrice;
    }

    public BigDecimal getInitialPrice() {
        return initialPrice;
    }

    public boolean eligibleForSale() {
        return LocalDate.now().isBefore(expiryDate);
    }

    public Long daysBeforeExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), this.expiryDate);
    }

    public ProductCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
