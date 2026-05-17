package com.mycompany.rpg.enemies;

public class Orc extends Enemy {

    public Orc() {
        super("Orc", 80, 15);
    }

@Override
public int attack() {
    int base = getAttackPower();
    int variation = (int)(Math.random() * 5); // random bonus 0–4
    int damage = base + variation;

    // 10% critical hit chance for enemies
    if (Math.random() < 0.1) {
        damage *= 2;
        System.out.println(getName() + " lands a CRITICAL HIT!");
    }

    return damage;
}
}