package com.mycompany.rpg.enemies;

public abstract class Enemy {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;

    // Status effects
    private boolean stunned = false;
    private boolean poisoned = false;
    private int poisonTurns = 0;

    public Enemy(String name, int hp, int attackPower) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttackPower() { return attackPower; }

    public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }

    // Status effect methods
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    public boolean isPoisoned() { return poisoned; }
    public int getPoisonTurns() { return poisonTurns; }
    public void setPoisoned(boolean poisoned, int turns) { 
        this.poisoned = poisoned; 
        this.poisonTurns = turns; 
    }

    // Abstract attack method
    public abstract int attack();
}