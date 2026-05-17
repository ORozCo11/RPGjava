package com.mycompany.rpg.items;

public class Item {
    private String name;
    private String description;
    private int effect; // e.g., healing amount

    public Item(String name, String description, int effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getEffect() { return effect; }
}