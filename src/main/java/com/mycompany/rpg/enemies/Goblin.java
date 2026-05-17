package com.mycompany.rpg.enemies;

public class Goblin extends Enemy {

    public Goblin() {
        super("Goblin", 50, 10);
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