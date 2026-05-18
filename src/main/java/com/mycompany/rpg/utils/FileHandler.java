package com.mycompany.rpg.utils;

import com.mycompany.rpg.characters.*;
import com.mycompany.rpg.characters.Character;
import com.mycompany.rpg.enemies.*;
import com.mycompany.rpg.items.Item;
import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    private static final String SAVE_FILE = "C:\\Users\\acer\\Documents\\savegame.txt";

    // ===== FIXED: NOW SAVES LIVE PARTY STATS, INVENTORY BAGS, AND ACTIVE ENEMY HEALTH POOLS =====
    public static void saveGame(Character[] party, int wave, int coins, ArrayList<Item> inventory, Enemy[] enemies) {
        System.out.println("[DEBUG] Attempting to save game...");
        File file = new File(SAVE_FILE);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Row 1: Core context wave status data
            writer.println(wave + "," + coins);

            // Mid Rows: Save ClassType, Name, CurrentHP, MaxHP, CurrentMP, MaxMP, Attack, Defense
            for (Character c : party) {
                writer.println(
                    c.getClass().getSimpleName() + "," +
                    c.getName() + "," +
                    c.getHp() + "," +
                    c.getMaxHp() + "," +
                    c.getMp() + "," +
                    c.getMaxMp() + "," +
                    c.getAttackPower() + "," +
                    c.getDefensePower()
                );
                System.out.println("[DEBUG] Saved Hero: " + c.getName() + " -> HP: " + c.getHp() + "/" + c.getMaxHp());
            }
            
            // Mid Rows: Save active Enemy states using the "ENEMY" identifier tag flag
            if (enemies != null) {
                for (Enemy e : enemies) {
                    writer.println("ENEMY," + e.getClass().getSimpleName() + "," + e.getName() + "," + e.getHp() + "," + e.getMaxHp());
                    System.out.println("[DEBUG] Saved Enemy: " + e.getName() + " -> HP: " + e.getHp() + "/" + e.getMaxHp());
                }
            }
            
            // Final Rows: Append Backpack item instances
            for (Item item : inventory) {
                writer.println("ITEM," + item.getName() + "," + item.getDescription() + "," + item.getEffect());
                System.out.println("[DEBUG] Saved Bag Item: " + item.getName());
            }
            
            writer.flush();
            System.out.println("[DEBUG] Game state text documents saved successfully!");
        } catch (IOException e) {
            System.out.println("[DEBUG] CRITICAL ERROR WHILE SAVING GAME:");
            e.printStackTrace();
        }
    }

    // ===== FIXED: EXTRACTS AND RESTORES MID- Encounter MONSTER ENTITIES PRECISELY =====
    public static LoadedData loadGame() {
        System.out.println("[DEBUG] Attempting to load game...");
        File file = new File(SAVE_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String metaLine = reader.readLine();
            if (metaLine == null) return null;
            
            String[] metaParts = metaLine.split(",");
            int wave = Integer.parseInt(metaParts[0]);
            int coins = Integer.parseInt(metaParts[1]);

            ArrayList<Character> loadedParty = new ArrayList<>();
            ArrayList<Enemy> loadedEnemies = new ArrayList<>();
            ArrayList<Item> loadedInventory = new ArrayList<>(); 
            
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split(",");
                if (parts.length == 0) continue;
                
                if (parts[0].equals("ITEM")) {
                    String itemName = parts[1];
                    String itemDesc = parts[2];
                    int itemEffect = Integer.parseInt(parts[3]);
                    loadedInventory.add(new Item(itemName, itemDesc, itemEffect));
                    System.out.println("[DEBUG] Loaded Bag Item: " + itemName);
                } else if (parts[0].equals("ENEMY")) {
                    // Re-instantiate custom damaged monster instances from text lines smoothly
                    String type = parts[1];
                    String name = parts[2];
                    int currentHp = Integer.parseInt(parts[3]);
                    int maxHp = Integer.parseInt(parts[4]);

                    // Automatically maps correct base game balancing damage values based on asset identity
                    int attackPower = switch (name) {
                        case "Acid Slime", "Toxic Slime" -> 8;
                        case "Goblin Scout", "Goblin Archer" -> 10;
                        case "Bone Grunt" -> 12;
                        case "Bone Mage" -> 14;
                        case "Lich Vanguard", "Orc Marauder" -> 15;
                        case "Goblin Assassin", "Fledgling Bat" -> 16;
                        case "Orc Warchief" -> 18;
                        case "Vampire Noble" -> 20;
                        case "⚡ TROLL WARLORD BOSS ⚡" -> 25;
                        default -> 10;
                    };

                    Enemy enemy = switch (type) {
                        case "Slime" -> new Slime(name, maxHp, attackPower);
                        case "Goblin" -> new Goblin(name, maxHp, attackPower);
                        case "Skeleton" -> new Skeleton(name, maxHp, attackPower);
                        case "Orc" -> new Orc(name, maxHp, attackPower);
                        case "Vampire" -> new Vampire(name, maxHp, attackPower);
                        case "Troll" -> new Troll(name, maxHp, attackPower);
                        default -> new Slime(name, maxHp, attackPower);
                    };

                    // Re-index remaining pools by calculating original matching damage deficit vectors safely
                    enemy.takeDamage(maxHp - currentHp);
                    loadedEnemies.add(enemy);
                    System.out.println("[DEBUG] Loaded Enemy: " + name + " -> HP: " + currentHp + "/" + maxHp);
                } else {
                    String type = parts[0];
                    String name = parts[1];
                    int currentHp = Integer.parseInt(parts[2]);
                    int maxHp = Integer.parseInt(parts[3]);
                    int currentMp = Integer.parseInt(parts[4]);
                    int maxMp = Integer.parseInt(parts[5]);
                    int attackPower = Integer.parseInt(parts[6]);
                    int defensePower = Integer.parseInt(parts[7]);

                    Character c = switch (type) {
                        case "Mage" -> new Mage(name, maxHp, maxMp, attackPower, defensePower);
                        case "Archer" -> new Archer(name, maxHp, maxMp, attackPower, defensePower);
                        default -> new Warrior(name, maxHp, maxMp, attackPower, defensePower); 
                    };
                    
                    c.setHp(currentHp);
                    c.setMp(currentMp);
                    loadedParty.add(c);
                    System.out.println("[DEBUG] Loaded Hero: " + name + " -> HP: " + currentHp + "/" + maxHp);
                }
            }

            return new LoadedData(
                loadedParty.toArray(new Character[0]), 
                wave, 
                coins, 
                loadedInventory, 
                loadedEnemies.toArray(new Enemy[0])
            );
        } catch (Exception e) {
            System.out.println("[DEBUG] CRITICAL ERROR WHILE LOADING GAME:");
            e.printStackTrace();
            return null;
        }
    }

    public static class LoadedData {
        public Character[] party;
        public int wave;
        public int coins;
        public ArrayList<Item> inventory; 
        public Enemy[] enemies; // Dynamic threat collection index array tracking slots

        public LoadedData(Character[] party, int wave, int coins, ArrayList<Item> inventory, Enemy[] enemies) {
            this.party = party;
            this.wave = wave;
            this.coins = coins;
            this.inventory = inventory;
            this.enemies = enemies;
        }
    }
}