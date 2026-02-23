package model;

public class Expense {

    private String payer;
    private double amount;
    private String description;

    private int id;

    public Expense(int id, String payer, double amount, String description) {
        this.id = id;
        this.payer = payer;
        this.amount = amount;
        this.description = description;
    }

    public int getId(){ return id; }

    public String getPayer() { return payer; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
}