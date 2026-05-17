package com.mycompany.rpg.characters;

public class Warrior extends Character {

    // Constructor now matches Character constructor with all stats
    public Warrior(String name, int maxHp, int maxMp, int attackPower, int defensePower) {
        super(name, maxHp, maxMp, attackPower, defensePower);
    }

    // You can also add a default constructor with standard stats
    public Warrior(String name) {
        super(name, 100, 50, 20, 10); // default HP, MP, attack, defense
    }

    // ===== Basic Attack =====
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

    // ===== Special Skill =====
    @Override
    public int useSkill() {
        // For example, Warrior skill is a powerful strike that costs MP
        int mpCost = 10;
        if (getMp() >= mpCost) {
            consumeMp(mpCost); // reduce MP
            int base = getAttackPower() + 15; // skill bonus
            int variation = (int)(Math.random() * 6);
            int damage = base + variation;

            // 20% critical hit chance
            if (Math.random() < 0.2) {
                damage *= 2;
                System.out.println(getName() + " lands a CRITICAL SKILL HIT!");
            }

            return damage;
        } else {
            System.out.println(getName() + " tried to use a skill but lacks MP!");
            return attack(); // fallback to basic attack
        }
    }

    // ===== Optional: Warrior passive or special trait =====
    public void taunt() {
        // Example: can force enemy to attack this warrior next turn
        System.out.println(getName() + " uses Taunt! Enemies are forced to target him.");
    }
}