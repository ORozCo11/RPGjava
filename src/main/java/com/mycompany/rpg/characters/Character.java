package com.mycompany.rpg.characters;

public abstract class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int mp;
    private int maxMp;
    private int attackPower;
    private int defensePower;

    // Status effects
    private boolean stunned = false;
    private boolean poisoned = false;
    private int poisonTurns = 0;

    public Character(String name, int maxHp, int maxMp, int attackPower, int defensePower) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.mp = maxMp;
        this.maxMp = maxMp;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getMp() { return mp; }
    public int getMaxMp() { return maxMp; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }

    public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    public void heal(int amount) { hp += amount; if (hp > maxHp) hp = maxHp; }

    public void consumeMp(int amount) {
        mp -= amount;
        if (mp < 0) mp = 0;
    }

    public void restoreMp(int amount) {
        mp += amount;
        if (mp > maxMp) mp = maxMp;
    }

    // Status effect methods
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    public boolean isPoisoned() { return poisoned; }
    public int getPoisonTurns() { return poisonTurns; }
    public void setPoisoned(boolean poisoned, int turns) { 
        this.poisoned = poisoned; 
        this.poisonTurns = turns; 
    }

    // Abstract methods
    public abstract int attack();
    public abstract int useSkill();
}