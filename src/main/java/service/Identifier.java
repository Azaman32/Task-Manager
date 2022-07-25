package main.java.service;

public class Identifier {
    private int id = 1;

    public int generate() {
        return id++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
