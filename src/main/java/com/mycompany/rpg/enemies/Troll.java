package com.mycompany.rpg.enemies;

public class Troll extends Enemy {
    
    public Troll(String name, int hp, int attackPower) {
        super(name, hp, attackPower);
    }

    @Override
    public int attack() {
        int base = getAttackPower();
        int variation = (int)(Math.random() * 12); // High damage volatility
        int totalDamage = base + variation;

        // Breathtaking Enrage Passive: If Boss HP is below 50%, deal 1.5x damage!
        if (getHp() < (getMaxHp() / 2)) {
            totalDamage = (int)(totalDamage * 1.5);
            System.out.println(getName() + " is ENRAGED! Damage output is massively boosted!");
        }

        // 15% Critical hit calculation
        if (Math.random() < 0.15) {
            totalDamage *= 2;
        }

        return totalDamage;
    }
}