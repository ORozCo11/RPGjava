package com.mycompany.rpg.characters;

public class Mage extends Character {

    // Constructor with custom stats
    public Mage(String name, int maxHp, int maxMp, int attackPower, int defensePower) {
        super(name, maxHp, maxMp, attackPower, defensePower);
    }

    // Default constructor with standard Mage stats
    public Mage(String name) {
        super(name, 80, 60, 30, 5); // HP, MP, attack, defense
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
        int mpCost = 15; // Mage skill costs more MP
        if (getMp() >= mpCost) {
            consumeMp(mpCost);

            int base = getAttackPower() + 20; // skill bonus
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

    // ===== Optional: Mage special trait =====
    public void castBuff() {
        // Example: Mage can buff party (future expansion)
        System.out.println(getName() + " casts a magical buff on the party!");
    }
}