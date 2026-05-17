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
import java.util.Arrays;

public class Main {

    private static Character[] party;
    private static Enemy[] enemies;
    private static ArrayList<Item> inventory;
    private static int coins = 0;
    private static BattleGUI gui;
    private static int currentCharacterIndex = 0;
    private static int currentWave = 0;

    public static void main(String[] args) {

       // ===== All possible characters =====
Character[] allCharacters = {
    new Warrior("Thor", 100, 50, 20, 10),
    new Mage("Merlin", 80, 60, 30, 5),
    new Archer("Legolas", 90, 55, 20, 8),
    new Warrior("Freya", 110, 45, 25, 12)
};
// ===== Select 2 characters for party =====
int choice1 = JOptionPane.showOptionDialog(
        null,
        "Select your first character",
        "Character Selection",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        Arrays.stream(allCharacters).map(Character::getName).toArray(),
        allCharacters[0].getName()
);


        int choice2 = JOptionPane.showOptionDialog(
        null,
        "Select your second character",
        "Character Selection",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        Arrays.stream(allCharacters).map(Character::getName).toArray(),
        allCharacters[1].getName()
);

        party = new Character[]{ allCharacters[choice1], allCharacters[choice2] };
        
        
        // ===== Initialize inventory =====
        inventory = new ArrayList<>();
        inventory.add(new Item("Health Potion", "Restores 30 HP", 30));
        inventory.add(new Item("Mana Elixir", "Restores 20 MP", 20));
        inventory.add(new Item("Revive Scroll", "Revives a fallen ally", 50));

       // ===== Create first wave =====
currentWave = 0;
enemies = createWave(currentWave);

        // ===== Initialize GUI =====
        gui = new BattleGUI(party, enemies);
        gui.setMessage("Wave " + (currentWave+1) + " begins! " + party[0].getName() + "'s turn");

        gui.onAttack(e -> handleAction("attack"));
        gui.onSkill(e -> handleAction("skill"));
        gui.onItem(e -> handleAction("item"));
        gui.onFlee(e -> handleAction("flee"));
    }

    // =========================================================
    private static void handleAction(String actionType) {
        Character currentChar = party[currentCharacterIndex];

        // Poison damage at start of turn
        if (currentChar.isPoisoned()) {
            int dmg = 5;
            currentChar.takeDamage(dmg);
            gui.updatePartyBars();
            gui.setMessage(currentChar.getName() + " takes " + dmg + " poison damage!");
            currentChar.setPoisoned(currentChar.getPoisonTurns() - 1 > 0, currentChar.getPoisonTurns()-1);
            if (!currentChar.isPoisoned()) gui.setMessage(currentChar.getName() + " is no longer poisoned.");
        }

        // Skip turn if stunned
        if (currentChar.isStunned()) {
            gui.setMessage(currentChar.getName() + " is stunned and skips the turn!");
            currentChar.setStunned(false);
            nextCharacterTurn();
            return;
        }

        // Choose target
        Enemy target = chooseEnemy();
        if (target == null) {
            gui.setMessage("No enemies left!");
            return;
        }

        // Player action
        switch (actionType) {
            case "attack" -> {
                int dmg = currentChar.attack();
                target.takeDamage(dmg);
                gui.updateEnemyBars();
                gui.setMessage(currentChar.getName() + " attacks " + target.getName() + " for " + dmg + " damage!");
            }
            case "skill" -> {
                int dmg = currentChar.useSkill();
                target.takeDamage(dmg);
                gui.updateEnemyBars();
                gui.setMessage(currentChar.getName() + " uses skill on " + target.getName() + " for " + dmg + " damage!");

                // Example: skill can stun enemy
                if (Math.random() < 0.2) { // 20% chance
                    target.setStunned(true);
                    gui.setMessage(target.getName() + " is stunned!");
                }
            }
            case "item" -> {
                if (!inventory.isEmpty()) {
                    String[] options = inventory.stream().map(i -> i.getName() + " (" + i.getDescription() + ")").toArray(String[]::new);
                    int choice = gui.askItemChoice("Choose an item:", options);
                    if (choice >= 0 && choice < inventory.size()) {
                        Item selected = inventory.remove(choice);
                        currentChar.heal(selected.getEffect());
                        gui.updatePartyBars();
                        gui.setMessage(currentChar.getName() + " uses " + selected.getName() + " and heals " + selected.getEffect() + " HP!");
                    }
                } else gui.setMessage("No items left!");
            }
           case "flee" -> {
    // 50% chance to successfully escape
    if (Math.random() < 0.5) {
        gui.setMessage(currentChar.getName() + " managed to escape the battle safely!");
        
        // Show a quick confirmation dialog before closing the app
        JOptionPane.showMessageDialog(gui, "You fled from battle! Returning to safety...", "Escaped", JOptionPane.INFORMATION_MESSAGE);
        
        // Closes the window and ends the program safely
        gui.dispose(); 
        System.exit(0); 
        return; // Break out early so nextCharacterTurn() isn't called!
    } else {
        gui.setMessage(currentChar.getName() + " tried to flee but couldn't get away!");
    }
}
        }

        nextCharacterTurn();
    }

    private static Enemy chooseEnemy() {
        ArrayList<Enemy> alive = new ArrayList<>();
        for (Enemy e : enemies) if (e.getHp() > 0) alive.add(e);
        if (alive.isEmpty()) return null;

        String[] options = alive.stream().map(e -> e.getName() + " (HP: " + e.getHp() + ")").toArray(String[]::new);
        int choice = gui.askEnemyChoice("Choose an enemy to attack:", options);
        return alive.get(choice);
    }

    private static void nextCharacterTurn() {
        currentCharacterIndex++;
        if (currentCharacterIndex >= party.length) {
            currentCharacterIndex = 0;
            enemyTurn();
        }
        Character c = party[currentCharacterIndex];
        gui.setMessage("It's " + c.getName() + "'s turn!");
        gui.updatePartyBars();
    }

    private static void enemyTurn() {
        for (Enemy e : enemies) {
            if (e.getHp() <= 0) continue;
            Character target = party[(int)(Math.random() * party.length)];
            int dmg = e.attack();
            target.takeDamage(dmg);
            gui.updatePartyBars();
            gui.setMessage(e.getName() + " attacks " + target.getName() + " for " + dmg + " damage!");
            if (Math.random() < 0.2) {
                target.setPoisoned(true, 3);
                gui.setMessage(target.getName() + " is poisoned for 3 turns!");
            }
        }

        checkWaveEnd();
    }

    private static void checkWaveEnd() {
        boolean allDead = true;
        for (Enemy e : enemies) if (e.getHp() > 0) allDead = false;

        if (allDead) {
            coins += Arrays.stream(enemies).mapToInt(e -> 10).sum();
            gui.setMessage("Wave " + (currentWave+1) + " cleared! Coins: " + coins);

            currentWave++;
            if (currentWave < 3) {
                Shop.openShop(gui, inventory, coins);
                enemies = createWave(currentWave);
                gui.setEnemies(enemies);
            } else gui.setMessage("All waves cleared! Congratulations!");
        }
    }

   // ===== Wave creation method =====
private static Enemy[] createWave(int wave) {
    return switch (wave) {
        case 0 -> new Enemy[]{ new Goblin("Goblin 1", 50, 10), new Goblin("Goblin 2", 50, 10) };
        case 1 -> new Enemy[]{ new Orc("Orc 1", 80, 15), new Goblin("Goblin 3", 50, 10) };
        default -> new Enemy[]{ new Orc("Orc 2", 80, 15), new Orc("Orc 3", 80, 15) };
    };
}
}
