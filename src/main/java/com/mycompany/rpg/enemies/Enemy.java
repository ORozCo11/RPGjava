package com.mycompany.rpg.enemies;

public abstract class Enemy {
    private String name;
    private int hp;
    private int maxHp;
    private int attackPower;

    // Status & Buff effects
    private boolean stunned = false;
    private boolean poisoned = false;
    private int poisonTurns = 0;
    private boolean attackBuffed = false; // Tracks if enemy used a Buff action

    public Enemy(String name, int hp, int attackPower) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    
    // Dynamically returns higher attack power if they are currently buffed
    public int getAttackPower() { 
        return attackBuffed ? (int)(attackPower * 1.5) : attackPower; 
    }

    public void takeDamage(int dmg) { hp -= dmg; if (hp < 0) hp = 0; }
    public void heal(int amount) { hp += amount; if (hp > maxHp) hp = maxHp; }

    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }

    public boolean isPoisoned() { return poisoned; }
    public int getPoisonTurns() { return poisonTurns; }
    public void setPoisoned(boolean poisoned, int turns) { 
        this.poisoned = poisoned; 
        this.poisonTurns = turns; 
    }

    public boolean isAttackBuffed() { return attackBuffed; }
    public void setAttackBuffed(boolean buffed) { this.attackBuffed = buffed; }

    // Abstract methods required by assignment rules
    public abstract int attack();
}