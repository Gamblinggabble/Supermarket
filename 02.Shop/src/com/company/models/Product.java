package com.company.models;

import com.company.helperClasses.ProductCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;

public class Product implements Serializable {

    //  В магазина се зареждат стоки, които ще се продават. Всяка стока се определя от
    // идентификационен номер, име, единична доставна цена и категория (хранителни и
    // нехранителни стоки). Освен това всяка стока има дата на изтичане на срока на годност.
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

    // Продажната цена на стоката се определя по следния начин:
    // 1. Хранителните и нехранителните стоки имат различен % надценка, който се определя в магазина.
    // 2. Ако срокът на годност наближава, т.е. остават по-малко от даден брой дни до
    // изтичането му, продажната цена на стоката се намалява с определен %. Броят на дните до
    // изтичането на срока и % намаление са различни за всеки магазин.
    // 3. Стоки с изтекъл срок на годност не трябва да се продават.
    public BigDecimal sellPrice(Map<ProductCategory, Double> percentsByCategory, Entry<Integer, Double> expiryDiscounts) {
        BigDecimal calculatedPrice = initialPrice;
        BigDecimal additional = initialPrice.multiply(BigDecimal.valueOf(percentsByCategory.get(this.category)));
        calculatedPrice = calculatedPrice.add(additional);

        if (daysBeforeExpiry() <= expiryDiscounts.getKey()) {
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
