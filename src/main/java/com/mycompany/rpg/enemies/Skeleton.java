package com.mycompany.rpg.enemies;

public class Skeleton extends Enemy {
    public Skeleton(String name, int hp, int attackPower) {
        super(name, hp, attackPower);
    }
    @Override
    public int attack() {
        int damage = getAttackPower() + (int)(Math.random() * 4);
        if (Math.random() < 0.1) damage *= 2; // 10% crit chance
        return damage;
    }
}