package com.company.models;

import com.company.helperClasses.Pair;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Receipt implements Serializable {
    private static final long serialVersionUUID = 1;
    private int id;
    private Cashier cashier;
    private LocalDateTime date;
    private Map<Product, Pair<Integer, BigDecimal>> productList; // product -> { quantity, sellPrice }
    private BigDecimal totalSum;
    private Supermarket supermarket;

    public Receipt(int id, Cashier cashier, LocalDateTime date, Supermarket supermarket) {
        this.id = id;
        this.cashier = cashier;
        this.date = date;
        this.supermarket = supermarket;

        this.productList = new HashMap<>();
        this.totalSum = BigDecimal.valueOf(0);
    }

    public void addProduct(Product product, Integer quantity) {
        if(!productList.containsKey(product))
            productList.put(product, new Pair<>(quantity, product.sellPrice(supermarket.getPercentAddedByCategory(), supermarket.getDaysBeforeExpiryForDiscount())));
        else{
            int initialQuantity = productList.get(product).getFirst();
            BigDecimal sellPrice = productList.get(product).getSecond();
            productList.put(product, new Pair<>(initialQuantity + quantity, sellPrice));
        }
        BigDecimal price = product.sellPrice(supermarket.getPercentAddedByCategory(), supermarket.getDaysBeforeExpiryForDiscount()).multiply(BigDecimal.valueOf(quantity));
        totalSum = totalSum.add(price);
    }

    public BigDecimal getTotalSum() {
        return totalSum;
    }

    public int getId() {
        return id;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Map<Product, Pair<Integer, BigDecimal>> getProductList() {
        return productList;
    }

    public Supermarket getSupermarket() {
        return supermarket;
    }
}
