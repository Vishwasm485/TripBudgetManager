package model;

public class Traveler {

    private int id;
    private String name;

    public Traveler(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}