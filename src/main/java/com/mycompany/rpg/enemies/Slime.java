package com.mycompany.rpg.enemies;

public class Slime extends Enemy {
    public Slime(String name, int hp, int attackPower) {
        super(name, hp, attackPower);
    }
    @Override
    public int attack() {
        // Slimes hit with standard constant physical impact
        return getAttackPower() + (int)(Math.random() * 3);
    }
}