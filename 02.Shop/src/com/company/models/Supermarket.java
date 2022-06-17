package com.company.models;

import com.company.helperClasses.CONSTANTS;
import com.company.helperClasses.ProductCategory;
import com.company.helperClasses.ReceiptsFileSaver;
import com.company.helperClasses.ReportsFileSaver;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

public class Supermarket implements Serializable {
    private String name;
    private Map<Product, Integer> productsDelivered;
    private Map<Product, Integer> productsInventory;
    private Map<Product, Integer> productsSold = new HashMap<>();
    private List<CashDesk> cashDesks;
    private List<Cashier> cashiers;
    private List<Receipt> receipts;
    private int receiptsCnt;

    private Map<ProductCategory, Double> percentAddedByCategory;
    private Entry<Integer, Double> daysBeforeExpiryDiscount;

    public Supermarket(String name, Map<ProductCategory, Double> percents, Entry<Integer, Double> daysToExpiryForDiscount) {
        this.name = name;
        this.percentAddedByCategory = percents;
        this.daysBeforeExpiryDiscount = daysToExpiryForDiscount;

        this.productsDelivered = new HashMap<>();
        this.productsInventory = new HashMap<>();
        this.cashiers = new ArrayList<>();
        this.cashDesks = new ArrayList<>();
        this.receipts = new ArrayList<>();
        this.receiptsCnt = 0;

        // create a directory for the store, receipts and reports
        new File(CONSTANTS.pathToStoresDirectories + this.name).mkdirs();
        new File(CONSTANTS.pathToStoresDirectories + this.name + CONSTANTS.pathToStoreReceiptsDirectories).mkdirs();
        new File(CONSTANTS.pathToStoresDirectories + this.name + CONSTANTS.pathToStoreReportsDirectories).mkdirs();
    }

    public String getName() {
        return name;
    }

    public Map<Product, Integer> getProductsInventory() {
        return productsInventory;
    }

    public Map<Product, Integer> getProductsSold() {
        return productsSold;
    }

    public int getReceiptsCnt() {
        return this.receipts.size();
    }

    public Map<ProductCategory, Double> getPercentAddedByCategory() {
        return percentAddedByCategory;
    }

    public Entry<Integer, Double> getDaysBeforeExpiryForDiscount() {
        return daysBeforeExpiryDiscount;
    }


    // methods for adding components
    public void addCashier(Cashier cashier) {
        cashiers.add(cashier);
    }

    public void addCashDesk(CashDesk cashDesk) {
        cashDesks.add(cashDesk);
    }

    public void addProductToInventory(Product product, int quantity) {
        // put in delivered list
        if (!productsDelivered.containsKey(product)) {
            productsDelivered.put(product, quantity);
        } else {
            int initialQuantity = productsDelivered.get(product);
            productsDelivered.put(product, initialQuantity + quantity);
        }

        // put in inventory list
        if (!productsInventory.containsKey(product)) {
            productsInventory.put(product, quantity);
        } else {
            int initialQuantity = productsInventory.get(product);
            productsInventory.put(product, initialQuantity + quantity);
        }
    }

    public void addReceipt(Receipt receipt) {
        this.receipts.add(receipt);

        // TODO create custom serialization for receipt's productList in order for the serialization to work
        //ReceiptsFileSaver.saveAsFileSer(receipt);
        ReceiptsFileSaver.saveAsFileTxt(receipt);
        ReceiptsFileSaver.readFromFileTxt(CONSTANTS.pathToStoresDirectories
                + receipt.getSupermarket().getName()
                + CONSTANTS.pathToStoreReceiptsDirectories
                + receipt.getId() + CONSTANTS.TXT);
    }


    // methods for reports
    public void makeReport() {
        LocalDateTime date = LocalDateTime.now();
        ReportsFileSaver.saveAsFileTxt(this, date);
        getReport(date);
    }

    public void getReport(LocalDateTime date) {
        ReportsFileSaver.readFromFileTxt(this.name, date);
    }

    public BigDecimal salariesForAMonth() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Cashier cashier : cashiers) {
            sum = sum.add(cashier.getMonthlySalary());
        }

        return sum;
    }

    public BigDecimal deliveriesCost() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Map.Entry<Product, Integer> entry : productsDelivered.entrySet()) {
            sum = sum.add(entry.getKey().getInitialPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return sum;
    }

    public BigDecimal incomeSoldProducts() {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Receipt receipt : receipts) {
            sum = sum.add(receipt.getTotalSum());
        }

        return sum;
    }

    public BigDecimal incomeSoldProductsByCategory(ProductCategory category) {
        BigDecimal sum = BigDecimal.valueOf(0);
        for (Map.Entry<Product, Integer> entry : productsSold.entrySet()) {
            if (entry.getKey().getCategory() == category)
                sum = sum.add(entry.getKey().sellPrice(percentAddedByCategory, daysBeforeExpiryDiscount).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return sum;
    }

    // methods for cashDesks start
    public void processClients(List<Client> clients) {
        // TODO make it work with different number of clients and cashDesks
        int numberOfCashDesks = this.cashDesks.size();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                cashDesks.get(0).takeClient(clients.get(0));
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                cashDesks.get(1).takeClient(clients.get(1));
            }
        });

        thread1.start();
        thread2.start();

        try{
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All clients have been served!");
    }

    public synchronized int getNextReceiptId() {
        return ++receiptsCnt;
    }
}
