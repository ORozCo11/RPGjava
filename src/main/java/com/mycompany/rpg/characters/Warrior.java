package com.mycompany.rpg.characters;

public class Warrior extends Character {

    public Warrior(String name) {
        super(name, 150, 25, 10); // example stats
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