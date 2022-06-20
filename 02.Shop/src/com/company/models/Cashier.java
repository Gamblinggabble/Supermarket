package com.company.models;

import java.math.BigDecimal;

public class Cashier {

    private int id;
    private String name;
    private BigDecimal monthlySalary;

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
