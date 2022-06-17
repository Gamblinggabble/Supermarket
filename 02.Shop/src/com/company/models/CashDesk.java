package com.company.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashDesk implements Serializable {
    private Cashier cashier;
    private Supermarket supermarket;

    public CashDesk(Cashier cashier, Supermarket supermarket) {
        this.cashier = cashier;
        this.supermarket = supermarket;
    }

    public void takeClient(Client client) {
        // getNextReceiptId() is synchronized
        Receipt receipt = new Receipt(supermarket.getNextReceiptId(), cashier, LocalDateTime.now(), supermarket);
        BigDecimal clientCash = client.getCash();
        for (var product : client.getProductList().keySet()) {
            try {
                BigDecimal productVal = BigDecimal.valueOf(client.getProductList().get(product))
                        .multiply(product.sellPrice(supermarket.getPercentAddedByCategory(), supermarket.getDaysBeforeExpiryForDiscount()));
                if (clientCash.compareTo(productVal) >= 0) {
                    this.scanProduct(product, client.getProductList().get(product), receipt);
                } else
                    throw new IllegalArgumentException("No sufficient funds to buy " + client.getProductList().get(product) + " " + product.getName());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        supermarket.addReceipt(receipt);
    }

    public synchronized void scanProduct(Product product, int quantity, Receipt receipt) {
        try {
            if (!supermarket.getProductsInventory().containsKey(product))
                throw new IllegalArgumentException("No such product in inventory!");
            else {
                if (supermarket.getProductsInventory().get(product) < quantity)
                    throw new IllegalArgumentException("Not enough quantity of " + product.getName() + " available in inventory!");
                else {
                    int initialQuantity = supermarket.getProductsInventory().get(product);
                    if (initialQuantity - quantity == 0) {
                        supermarket.getProductsInventory().remove(product);
                    } else supermarket.getProductsInventory().put(product, initialQuantity - quantity);

                    if (!supermarket.getProductsSold().containsKey(product))
                        supermarket.getProductsSold().put(product, quantity);
                    else {
                        int initialQuantitySold = supermarket.getProductsSold().get(product);
                        supermarket.getProductsSold().put(product, initialQuantitySold + quantity);
                    }

                    System.out.println(product.getName());
                    receipt.addProduct(product, quantity);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
