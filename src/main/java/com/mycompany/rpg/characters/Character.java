package com.mycompany.rpg.characters;

public abstract class Character {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;
    private int defensePower;

    // Status effects
    private boolean stunned = false;
    private boolean poisoned = false;
    private int poisonTurns = 0;

    public Character(String name, int maxHp, int attackPower, int defensePower) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }

    public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    public void heal(int amount) { hp += amount; if (hp > maxHp) hp = maxHp; }

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