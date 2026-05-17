package com.mycompany.rpg.main;

import com.mycompany.rpg.characters.Character;
import com.mycompany.rpg.characters.Warrior;
import com.mycompany.rpg.characters.Mage;
import com.mycompany.rpg.characters.Archer;

import com.mycompany.rpg.enemies.*;
import com.mycompany.rpg.items.*;
import com.mycompany.rpg.ui.BattleGUI;
import com.mycompany.rpg.utils.FileHandler;
import com.mycompany.rpg.utils.Shop;

import javax.swing.*;
import java.util.ArrayList;

public class Main {

    private static Character[] party;
    private static Enemy[] enemies;
    private static ArrayList<Item> inventory;
    private static int coins = 0;
    private static BattleGUI gui;
    private static int currentCharacterIndex = 0;
    private static int currentWave = 0;

    public static void main(String[] args) {

        // ===== Initialize party =====
        party = new Character[]{
                new Warrior("Thor"),
                new Mage("Merlin"),
                new Archer("Legolas")
        };

        // ===== Initialize inventory =====
        inventory = new ArrayList<>();
        inventory.add(new Item("Health Potion", "Restores 30 HP", 30));
        inventory.add(new Item("Mana Elixir", "Restores 20 MP", 20));
        inventory.add(new Item("Revive Scroll", "Revives a fallen ally", 50));

        // ===== Initialize first wave =====
        currentWave = 0;
        enemies = createWave(currentWave);

        // ===== Initialize GUI =====
        gui = new BattleGUI(
                party[0].getName(),
                party[0].getHp(),
                enemies[0].getName(),
                enemies[0].getHp()
        );
        gui.setMessage("Wave 1 begins! " + party[0].getName() + "'s turn");

        // ===== Button listeners =====
        gui.onAttack(e -> handleAction("attack"));
        gui.onSkill(e -> handleAction("skill"));
        gui.onItem(e -> handleAction("item"));
        gui.onFlee(e -> handleAction("flee"));
    }

    private static void handleAction(String actionType) {
        Character currentChar = party[currentCharacterIndex];

        // ===== Check poison status at start of turn =====
        if (currentChar.isPoisoned()) {
            int poisonDmg = 5;
            currentChar.takeDamage(poisonDmg);
            gui.setPlayerHp(currentChar.getHp());
            gui.setMessage(currentChar.getName() + " takes " + poisonDmg + " poison damage!");

            currentChar.setPoisoned(true, currentChar.getPoisonTurns() - 1);
            if (currentChar.getPoisonTurns() <= 0) {
                currentChar.setPoisoned(false, 0);
                gui.setMessage(currentChar.getName() + " is no longer poisoned.");
            }
        }

        // ===== Check stun status =====
        if (currentChar.isStunned()) {
            gui.setMessage(currentChar.getName() + " is stunned and skips the turn!");
            currentChar.setStunned(false);
            nextCharacterTurn();
            return;
        }

        // ===== Player action =====
        Enemy target = chooseEnemy(enemies);
        if (target == null) {
            gui.setMessage("No enemies left!");
            return;
        }

        switch (actionType) {
            case "attack":
                int dmg = currentChar.attack();
                target.takeDamage(dmg);
                gui.setEnemyHp(target.getHp());
                gui.setMessage(currentChar.getName() + " attacks " + target.getName() + " for " + dmg + " damage!");
                break;

            case "skill":
                int skillDmg = currentChar.useSkill();
                target.takeDamage(skillDmg);
                gui.setEnemyHp(target.getHp());
                gui.setMessage(currentChar.getName() + " uses skill on " + target.getName() + " for " + skillDmg + " damage!");

                // Example: Player skill can stun enemy
                if (Math.random() < 0.2) { // 20% chance
                    target.setStunned(true);
                    gui.setMessage(target.getName() + " is stunned!");
                }
                break;

            case "item":
                if (!inventory.isEmpty()) {
                    String[] options = new String[inventory.size()];
                    for (int i = 0; i < inventory.size(); i++) {
                        Item item = inventory.get(i);
                        options[i] = item.getName() + " (" + item.getDescription() + ")";
                    }
                    int choice = gui.askItemChoice("Choose an item to use:", options);
                    if (choice >= 0 && choice < inventory.size()) {
                        Item selected = inventory.remove(choice);
                        currentChar.heal(selected.getEffect());
                        gui.setPlayerHp(currentChar.getHp());
                        gui.setMessage(currentChar.getName() + " uses " + selected.getName() +
                                " and heals " + selected.getEffect() + " HP!");
                    }
                } else {
                    gui.setMessage("No items left!");
                }
                break;

            case "flee":
                gui.setMessage(currentChar.getName() + " tried to flee!");
                break;
        }

        // ===== Enemy turn =====
        for (Enemy e : enemies) {
            if (e.getHp() <= 0) continue;

            Character enemyTarget = choosePartyMember(party);
            if (enemyTarget != null) {
                int dmgTaken = e.attack();
                enemyTarget.takeDamage(dmgTaken);
                gui.setPlayerHp(enemyTarget.getHp());
                gui.setMessage(e.getName() + " attacks " + enemyTarget.getName() + " for " + dmgTaken + " damage!");

                // Enemy skill can poison player
                if (Math.random() < 0.2) {
                    enemyTarget.setPoisoned(true, 3);
                    gui.setMessage(enemyTarget.getName() + " is poisoned for 3 turns!");
                }
            }
        }

        // ===== Check if wave is over =====
        boolean allEnemiesDead = true;
        for (Enemy e : enemies) if (e.getHp() > 0) allEnemiesDead = false;

        if (allEnemiesDead) {
            coins += enemies.length * 10;
            gui.setMessage("Wave " + (currentWave + 1) + " cleared! Coins: " + coins);

            // Shop between waves
            currentWave++;
            if (currentWave < 3) {
                Shop.openShop(gui, inventory, coins);
                enemies = createWave(currentWave);
                gui.setEnemyHp(enemies[0].getHp());
            } else {
                gui.setMessage("All waves cleared! Congratulations!");
                FileHandler.saveGame(party, "savegame.txt");
            }
        }

        nextCharacterTurn();
    }

    private static void nextCharacterTurn() {
        currentCharacterIndex++;
        if (currentCharacterIndex >= party.length) currentCharacterIndex = 0;
        Character c = party[currentCharacterIndex];
        gui.setMessage("It's " + c.getName() + "'s turn!");
        gui.setPlayerHp(c.getHp());
    }

    private static Enemy chooseEnemy(Enemy[] enemies) {
        ArrayList<Enemy> aliveEnemies = new ArrayList<>();
        for (Enemy e : enemies) if (e.getHp() > 0) aliveEnemies.add(e);

        if (aliveEnemies.isEmpty()) return null;

        String[] options = new String[aliveEnemies.size()];
        for (int i = 0; i < aliveEnemies.size(); i++) {
            options[i] = aliveEnemies.get(i).getName() + " (HP: " + aliveEnemies.get(i).getHp() + ")";
        }

        int choice = gui.askEnemyChoice("Choose an enemy to attack:", options);
        return aliveEnemies.get(choice);
    }

    private static Character choosePartyMember(Character[] party) {
        for (Character c : party) if (c.getHp() > 0) return c;
        return null;
    }

    private static Enemy[] createWave(int waveIndex) {
        switch (waveIndex) {
            case 0: return new Enemy[]{ new Goblin(), new Goblin() };
            case 1: return new Enemy[]{ new Orc(), new Goblin() };
            case 2: return new Enemy[]{ new Orc(), new Orc() };
            default: return new Enemy[]{};
        }
    }
}