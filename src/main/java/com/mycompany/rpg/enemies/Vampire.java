package com.mycompany.rpg.enemies;

public class Vampire extends Enemy {
    public Vampire(String name, int hp, int attackPower) {
        super(name, hp, attackPower);
    }
    @Override
    public int attack() {
        int damage = getAttackPower() + (int)(Math.random() * 6);
        // Vampires possess life-steal: they heal themselves for half the damage dealt!
        this.heal(damage / 2);
        return damage;
    }
}