package com.example.electratrack;

public class Transaction {
    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Transaction(int amount, String description, String category,String date) {
        Amount = amount;
        Description = description;
        Category = category;
        Date=date;
    }
public  Transaction(){

}
    private int Amount;
    private String Description;
   private String Category;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    private String Date;

}
