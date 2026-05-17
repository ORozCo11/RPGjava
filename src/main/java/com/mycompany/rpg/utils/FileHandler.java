package com.mycompany.rpg.utils;

import com.mycompany.rpg.characters.Character;
import java.io.*;

public class FileHandler {

    public static void saveGame(Character[] party, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Character c : party) {
                writer.println(c.getName() + "," + c.getHp());
            }
            System.out.println("Game saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public static void loadGame(Character[] party, String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null && index < party.length) {
                String[] parts = line.split(",");
                party[index].heal(Integer.parseInt(parts[1]) - party[index].getHp());
                index++;
            }
            System.out.println("Game loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }
}