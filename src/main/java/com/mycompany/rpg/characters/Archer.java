package com.mycompany.rpg.characters;

public class Archer extends Character {

    // Constructor with custom stats
    public Archer(String name, int maxHp, int maxMp, int attackPower, int defensePower) {
        super(name, maxHp, maxMp, attackPower, defensePower);
    }

    // Default constructor with standard Archer stats
    public Archer(String name) {
        super(name, 90, 55, 25, 8); // default HP, MP, attack, defense
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
        int mpCost = 12; // Archer skill cost
        if (getMp() >= mpCost) {
            consumeMp(mpCost); // reduce MP
            int base = getAttackPower() + 18; // skill bonus
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
            return attack(); // fallback to normal attack
        }
    }

    // ===== Optional Archer trait =====
    public void longShot() {
        // Example: Archer special move
        System.out.println(getName() + " uses Long Shot!");
    }
}