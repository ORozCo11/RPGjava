package com.mycompany.rpg.characters;

public class Mage extends Character {

    public Mage(String name) {
        super(name, 100, 15, 5);
    }

 @Override
public int attack() {
    int base = getAttackPower();
    int variation = (int)(Math.random() * 6); // random bonus 0–5
    int damage = base + variation;

    // 20% critical hit chance
    if (Math.random() < 0.2) {
        damage *= 2;
        System.out.println(getName() + " lands a CRITICAL HIT!");
    }
    return damage;
}

@Override
public int useSkill() {
    int base = getAttackPower() + 15; // skill bonus
    int variation = (int)(Math.random() * 6);
    int damage = base + variation;

    // 20% critical hit chance
    if (Math.random() < 0.2) {
        damage *= 2;
        System.out.println(getName() + " lands a CRITICAL SKILL HIT!");
    }
    return damage;
}
}