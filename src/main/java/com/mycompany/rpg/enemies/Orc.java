package com.mycompany.rpg.enemies;

public class Orc extends Enemy {

    public Orc() {
        super("Orc", 80, 15); // default
    }

    public Orc(String name, int hp, int attackPower) {
        super(name, hp, attackPower); // parameterized
    }

    @Override
    public int attack() {
        int base = getAttackPower();
        int variation = (int)(Math.random() * 5);
        int damage = base + variation;
        if (Math.random() < 0.1) damage *= 2;
        return damage;
    }
}