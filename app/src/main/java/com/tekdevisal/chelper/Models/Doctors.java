package com.tekdevisal.chelper.Models;

public class Doctors {
    public String id;
    public String name;

    public Doctors(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
