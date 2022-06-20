package com.company.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Client {

    private BigDecimal cash;
    private Map<Product, Integer> productList;

    public Client(BigDecimal cash) {
        if (cash.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Cash must be a positive number");
        this.cash = cash;
        this.productList = new HashMap<>();
    }

    public BigDecimal getCash() {
        return cash;
    }

    public Map<Product, Integer> getProductList() {
        return productList;
    }

    public void addProductToCart(Product product, int quantity) {
        if (!productList.containsKey(product)) productList.put(product, quantity);
        else {
            int initialQuantity = productList.get(product);
            productList.put(product, initialQuantity + quantity);
        }
    }

}
