package com.company;

import com.company.helperClasses.ProductCategory;
import com.company.models.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static void main(String[] args) {

        Product bread = new Product(1, "bread", BigDecimal.valueOf(1.0), ProductCategory.FOOD, LocalDate.now().plusDays(5));
        Product chocolate = new Product(2, "chocolate", BigDecimal.valueOf(3.0), ProductCategory.FOOD, LocalDate.now().plusYears(1));
        Product chocolate2 = new Product(3, "chocolate expired", BigDecimal.valueOf(3.0), ProductCategory.FOOD, LocalDate.now().plusDays(1));
        Product hummus = new Product(4, "hummus", BigDecimal.valueOf(2.5), ProductCategory.FOOD, LocalDate.now().plusDays(10));
        Product hummus2 = new Product(5, "hummus2", BigDecimal.valueOf(2.5), ProductCategory.FOOD, LocalDate.now().plusDays(10));
        Product hummus3 = new Product(6, "hummus3", BigDecimal.valueOf(2.5), ProductCategory.FOOD, LocalDate.now().plusDays(10));
        Product hummus4 = new Product(7, "hummus4", BigDecimal.valueOf(2.5), ProductCategory.FOOD, LocalDate.now().plusDays(10));
        Product hummus5 = new Product(8, "hummus5", BigDecimal.valueOf(2.5), ProductCategory.FOOD, LocalDate.now().plusDays(10));
        Product shampoo = new Product(9, "shampoo", BigDecimal.valueOf(10.0), ProductCategory.NON_FOOD, LocalDate.now().plusYears(20));

        Cashier cashier1 = new Cashier(1, "Ivan Ivanov", BigDecimal.valueOf(1000));
        Cashier cashier2 = new Cashier(2, "Petar Ivanov", BigDecimal.valueOf(1100));

        Client client1 = new Client(BigDecimal.valueOf(10000));
        {
            client1.addProductToCart(bread, 50);
            client1.addProductToCart(chocolate, 1);
            client1.addProductToCart(chocolate2, 1);
        }
        Client client2 = new Client(BigDecimal.valueOf(10000));
        {
            client2.addProductToCart(hummus, 1);
            client2.addProductToCart(hummus2, 2);
            client2.addProductToCart(hummus3, 3);
            client2.addProductToCart(hummus4, 4);
            client2.addProductToCart(hummus5, 5);
        }

        Client client3 = new Client(BigDecimal.valueOf(1000));
        client3.addProductToCart(shampoo, 1);

        BlockingQueue<Client> clients = new ArrayBlockingQueue<>(3);
        {
            clients.add(client1);
            clients.add(client2);
            clients.add(client3);
        }

        Map<ProductCategory, Double> fantasticoPercentsAdded = new HashMap<>() {{
            put(ProductCategory.FOOD, 0.1);
            put(ProductCategory.NON_FOOD, 0.2);
        }};
        Entry<Integer, Double> fantasticoExpiryPolicy = Map.entry(2, 0.5); // 2 days or less left before expiry, 50% discount
        Supermarket fantastico = new Supermarket("Fantastico", fantasticoPercentsAdded, fantasticoExpiryPolicy);
        {
            fantastico.addProductToInventory(bread, 50);
            fantastico.addProductToInventory(chocolate, 70);
            fantastico.addProductToInventory(chocolate2, 10);
            fantastico.addProductToInventory(hummus, 50);
            fantastico.addProductToInventory(hummus2, 50);
            fantastico.addProductToInventory(hummus3, 50);
            fantastico.addProductToInventory(hummus4, 50);
            fantastico.addProductToInventory(hummus5, 50);
            fantastico.addProductToInventory(shampoo, 10);
            fantastico.addCashier(cashier1);
            fantastico.addCashier(cashier2);
        }

        CashDesk cashDesk1 = new CashDesk(cashier1, fantastico);
        CashDesk cashDesk2 = new CashDesk(cashier2, fantastico);
        fantastico.addCashDesk(cashDesk1);
        fantastico.addCashDesk(cashDesk2);

        fantastico.processClients(clients);
        fantastico.makeReport();
    }
}
