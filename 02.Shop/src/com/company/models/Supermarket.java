package com.company.models;

import com.company.exceptions.ExpiredProductException;
import com.company.helperClasses.CONFIG;
import com.company.helperClasses.ProductCategory;
import com.company.helperClasses.ReceiptsFileSaver;
import com.company.helperClasses.ReportsFileSaver;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

public class Supermarket {

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

        // create a directory for the store
        new File(CONFIG.PATH_TO_STORES_DIR + this.name).mkdirs();
        //  receipts folder
        new File(CONFIG.PATH_TO_STORES_DIR + this.name + CONFIG.PATH_TO_RECEIPTS_DIR).mkdirs();
        //  reports folder
        new File(CONFIG.PATH_TO_STORES_DIR + this.name + CONFIG.PATH_TO_REPORTS_DIR).mkdirs();
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
        if (quantity <= 0) throw new IllegalArgumentException("Quantity of products added must be a positive number.");

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

        //ReceiptsFileSaver.saveAsFileSer(receipt);
        ReceiptsFileSaver.saveAsFileTxt(receipt);
        ReceiptsFileSaver.readFromFileTxt(CONFIG.PATH_TO_STORES_DIR
                + receipt.getSupermarket().getName()
                + CONFIG.PATH_TO_RECEIPTS_DIR
                + receipt.getId() + CONFIG.TXT);

        ReceiptsFileSaver.saveAsFileHtml(receipt);
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
            if (entry.getKey().getCategory() == category) {
                try {
                    sum = sum.add(entry.getKey().sellPrice(percentAddedByCategory, daysBeforeExpiryDiscount).multiply(BigDecimal.valueOf(entry.getValue())));
                } catch (ExpiredProductException e) {
                    e.printStackTrace();
                }
            }
        }
        return sum;
    }


    // methods for cashDesks start

    // works with 2 cash desks only
    public void processClientsBasic(BlockingQueue<Client> clients) {
        // TODO make it work with different number of clients and cashDesks
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!clients.isEmpty()) {
                    try {
                        cashDesks.get(0).takeClient(clients.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!clients.isEmpty()) {
                    try {
                        cashDesks.get(1).takeClient(clients.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All clients have been served!");
    }

    public void processClients(BlockingQueue<Client> clients) {
        int numberOfCashDesks = this.cashDesks.size();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numberOfCashDesks; i++) {
            int cashDeskIndex = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!clients.isEmpty()) {
                        try {
                            cashDesks.get(cashDeskIndex).takeClient(clients.take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }));
        }

        for (int i = 0; i < numberOfCashDesks; i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < numberOfCashDesks; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All clients have been served!");
    }

    public synchronized int getNextReceiptId() {
        return ++receiptsCnt;
    }
}
