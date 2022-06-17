package com.company.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cashier implements Serializable {

    private int id;
    private String name;
    private BigDecimal monthlySalary;
//    private CashDesk cashDesk;

    public Cashier(int id, String name, BigDecimal monthlySalary) {
        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }
}
