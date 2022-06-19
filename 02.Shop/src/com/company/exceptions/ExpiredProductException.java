package com.company.exceptions;

public class ExpiredProductException extends Exception {
    private long daysAfterExpired;

    public ExpiredProductException(long daysAfterExpired) {
        this.daysAfterExpired = daysAfterExpired;
    }

    @Override
    public String toString() {
        return "com.company.exceptions.ExpiredProductException{" + "the product expired " + this.daysAfterExpired + " days ago. Expired products are not eligible for sale!" + '}';
    }
}
