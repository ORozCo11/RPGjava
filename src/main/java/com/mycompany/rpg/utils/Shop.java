package com.mycompany.rpg.utils;

import com.mycompany.rpg.items.Item;
import com.mycompany.rpg.ui.BattleGUI;
import javax.swing.*;
import java.util.ArrayList;

public class Shop {

    public static void openShop(BattleGUI gui, ArrayList<Item> inventory, int coins) {
        gui.setMessage("Welcome to the Shop! You have " + coins + " coins.");

        ArrayList<Item> shopItems = new ArrayList<>();
        shopItems.add(new Item("Health Potion", "Restores 30 HP", 30));
        shopItems.add(new Item("Mana Elixir", "Restores 20 MP", 20));
        shopItems.add(new Item("Revive Scroll", "Revives a fallen ally", 50));

        // Convert shopItems to strings for selection
        String[] options = new String[shopItems.size()];
        for (int i = 0; i < shopItems.size(); i++) {
            Item item = shopItems.get(i);
            options[i] = item.getName() + " (" + item.getDescription() + ") - 10 coins";
        }

        boolean shopping = true;
        while (shopping && coins >= 10) {
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select an item to buy:",
                    "Shop",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice >= 0 && choice < shopItems.size()) {
                Item selected = shopItems.get(choice);
                if (coins >= 10) {
                    inventory.add(selected);
                    coins -= 10;
                    gui.setMessage("Bought " + selected.getName() + " for 10 coins.");
                } else {
                    gui.setMessage("Not enough coins!");
                }
            } else {
                // Player closed the dialog or clicked cancel
                shopping = false;
            }
        }

        gui.setMessage("Shop closed. Remaining coins: " + coins);
    }
}