package model;

public class Trip {

    private int id;
    private String name;
    private int days;
    private double budget;

    public Trip(int id, String name, int days, double budget) {
        this.id = id;
        this.name = name;
        this.days = days;
        this.budget = budget;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDays() { return days; }
    public double getBudget() { return budget; }
}