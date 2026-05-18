package com.mycompany.rpg.main;

import com.mycompany.rpg.characters.Character;
import com.mycompany.rpg.characters.*;
import com.mycompany.rpg.enemies.*;
import com.mycompany.rpg.items.*;
import com.mycompany.rpg.ui.BattleGUI;
import com.mycompany.rpg.ui.MainMenuGUI;
import com.mycompany.rpg.utils.FileHandler;
import com.mycompany.rpg.utils.Shop;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static Character[] party;
    private static Enemy[] enemies;
    private static ArrayList<Item> inventory;
    private static int coins = 0;
    private static BattleGUI gui;
    private static MainMenuGUI menuGui;
    private static int currentCharacterIndex = 0;
    private static int currentWave = 0;

    // Assignment Rubric Scorecard Counters
    private static int totalTurnsTaken = 0;
    private static int totalEnemiesDefeated = 0;

    public static void main(String[] args) {
        initGameEnvironment();
    }

    private static void initGameEnvironment() {
        inventory = new ArrayList<>();
        inventory.add(new Item("Health Potion", "Restores 30 HP", 30));
        inventory.add(new Item("Mana Elixir", "Restores 20 MP", 20));
        inventory.add(new Item("Revive Scroll", "Revives a fallen ally", 50));

        menuGui = new MainMenuGUI();
        menuGui.onNewGame(e -> startNewGame());
        menuGui.onLoadGame(e -> loadExistingGame());
        menuGui.onExitGame(e -> System.exit(0));
    }

    private static void startNewGame() {
        menuGui.dispose();

        // ===== CHARACTER INITIAL STAT POOLS =====
        Character[] allCharacters = {
            new Warrior("Thor", 300, 160, 25, 10),     
            new Mage("Merlin", 250, 220, 30, 5),       
            new Archer("Legolas", 280, 180, 22, 8),    
            new Warrior("Freya", 310, 150, 26, 12)     
        };

        new com.mycompany.rpg.ui.CharacterSelectionGUI(allCharacters, chosenParty -> {
            party = chosenParty;
            currentWave = 0;
            coins = 0;
            currentCharacterIndex = 0; 
            totalTurnsTaken = 0;
            totalEnemiesDefeated = 0;
            
            showWaveStartBreakPrompt();
            launchBattle();
        });
    }

    private static void loadExistingGame() {
        FileHandler.LoadedData data = FileHandler.loadGame();
        if (data != null) {
            menuGui.dispose();
            party = data.party;
            currentWave = data.wave;
            coins = data.coins;
            
            if (data.inventory != null) {
                inventory = data.inventory;
            }
            
            // ===== FIXED: ENEMY PERSISTENCE VERIFICATION LOOP =====
            // Only restores the enemy array if saved mid-encounter with living threats!
            if (data.enemies != null && data.enemies.length > 0 && Arrays.stream(data.enemies).anyMatch(e -> e.getHp() > 0)) {
                enemies = data.enemies;
            } else {
                enemies = null; // Forces launchBattle to fetch a fresh full wave safely
            }
            
            currentCharacterIndex = 0; 
            
            showWaveStartBreakPrompt();
            launchBattle();
        } else {
            showCustomMessageDialog("ERROR", "No valid save data file found! Start a New Game instead.");
        }
    }

   private static void launchBattle() {
        // ===== FIXED: PREVENTS OVERWRITING EXISTING MID-ATTACK ENEMY POOLS =====
        if (enemies == null) {
            enemies = createWave(currentWave);
        }
        gui = new BattleGUI(party, enemies);
        gui.updateCoinCount(coins);

        gui.onWindowCloseRequest(Main::executeGameSave);

        // ===== RETURN TO MAIN MENU FLOW MANAGER =====
        gui.onReturnToMainMenu(e -> {
            String[] menuChoices = {"Confirm Return", "Stay and Fight"};
            int choice = gui.showThemedConfirmDialog(
                    "RETURN TO MAIN MENU?", 
                    "Are you sure you want to save progress and return to the Main Menu?\nYour stats and coins will be preserved.", 
                    menuChoices
            );
            
            if (choice == 0) { 
                executeGameSave();
                gui.dispose(); 
                SwingUtilities.invokeLater(Main::initGameEnvironment); 
            }
        });

        gui.onAttack(e -> handleAction("attack"));
        gui.onSkill(e -> handleAction("skill"));
        gui.onItem(e -> handleAction("item"));
        gui.onFlee(e -> handleAction("flee"));

        gui.setMessage("Wave " + (currentWave + 1) + " begins! " + party[currentCharacterIndex].getName() + "'s turn");
        gui.updatePartyBars();
        gui.updateEnemyBars();
        
        gui.setActiveTurn(currentCharacterIndex, -1);
    }

    private static void showWaveStartBreakPrompt() {
        Color COLOR_BG_PANEL     = new Color(42, 45, 62);   
        Color COLOR_BG_CARD      = new Color(52, 56, 77);   
        Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248); 
        Color COLOR_TEXT_MUTED   = new Color(174, 190, 209);   
        Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105); 
        Color COLOR_BORDER       = new Color(80, 86, 118);  

        JPanel customPromptPanel = new JPanel(new BorderLayout(0, 12));
        customPromptPanel.setBackground(COLOR_BG_PANEL);
        customPromptPanel.setPreferredSize(new Dimension(420, 240));
        customPromptPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleBannerLabel = new JLabel("⚔ COMBAT READY CHECK ⚔", SwingConstants.CENTER);
        titleBannerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleBannerLabel.setForeground(COLOR_ACCENT_GOLD);
        titleBannerLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        customPromptPanel.add(titleBannerLabel, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new GridLayout(4, 1, 0, 4));
        centerCard.setBackground(COLOR_BG_CARD);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
                ));

        String stageTitleText = (currentWave == 3) ? "FINAL BOSS ROOM ENCOUNTER" : "STAGE WAVE CHALLENGE " + (currentWave + 1) + " / 4";
        JLabel stageHeaderLabel = new JLabel(stageTitleText, SwingConstants.CENTER);
        stageHeaderLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        stageHeaderLabel.setForeground(COLOR_TEXT_LIGHT);

        JLabel breakReminderLabel = new JLabel("Take a break if you need it! Progress is autosaved.", SwingConstants.CENTER);
        breakReminderLabel.setFont(new Font("Monospaced", Font.ITALIC, 12));
        breakReminderLabel.setForeground(COLOR_TEXT_MUTED);

        JPanel financialRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        financialRow.setOpaque(false);
        JLabel walletText = new JLabel("Current Savings Balance:");
        walletText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        walletText.setForeground(COLOR_TEXT_LIGHT);
        
        ImageIcon coinGraphic = loadInternalAssetStreamIcon("coin.png", 16, 16);
        JLabel coinGraphicLabel = (coinGraphic != null) ? new JLabel(coinGraphic) : new JLabel("🪙");
        
        JLabel financialSumLabel = new JLabel(coins + " Gold Coins");
        financialSumLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        financialSumLabel.setForeground(COLOR_ACCENT_GOLD);
        financialRow.add(walletText);
        financialRow.add(coinGraphicLabel);
        financialRow.add(financialSumLabel);

        JLabel actionInfoLabel = new JLabel("Press the action button down below to begin combat.", SwingConstants.CENTER);
        actionInfoLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        actionInfoLabel.setForeground(COLOR_TEXT_MUTED);

        centerCard.add(stageHeaderLabel);
        centerCard.add(breakReminderLabel);
        centerCard.add(financialRow);
        centerCard.add(actionInfoLabel);
        customPromptPanel.add(centerCard, BorderLayout.CENTER);

        JButton startBtn = new JButton("⚔ START WAVE ⚔");
        startBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        startBtn.setBackground(new Color(69, 74, 102)); 
        startBtn.setForeground(COLOR_TEXT_LIGHT);
        startBtn.setFocusPainted(false);
        startBtn.setPreferredSize(new Dimension(0, 42));
        startBtn.setBorder(new LineBorder(COLOR_BORDER, 1, true));

        startBtn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(startBtn);
            if (win != null) win.dispose();
        });
        customPromptPanel.add(startBtn, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_LIGHT);

        JOptionPane.showOptionDialog(
                gui, customPromptPanel, "RPG ENGINE - INTERMISSION PAUSE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );
    }

    private static ImageIcon loadInternalAssetStreamIcon(String assetName, int targetW, int targetH) {
        try {
            String path = "/com/mycompany/rpg/assets/" + assetName;
            InputStream is = Main.class.getResourceAsStream(path);
            if (is == null) {
                is = Main.class.getResourceAsStream("/assets/" + assetName);
            }
            if (is == null) return null;
            Image raw = ImageIO.read(is);
            is.close();
            if (raw == null) return null;
            return new ImageIcon(raw.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }

    public static void executeGameSave() {
        if (party != null) {
            System.out.println("[MAIN DEBUG] executeGameSave running! Wave: " + currentWave + ", Coins: " + coins);
            // ===== FIXED: DISPATCHES LIVE ENEMY REFERENCE STATE FOR SYNCHRONIZATION FIELD WRITES =====
            FileHandler.saveGame(party, currentWave, coins, inventory, enemies);
        } else {
            System.out.println("[MAIN DEBUG] Save failed: Party array object is null!");
        }
    }

    private static void handleAction(String actionType) {
        Character currentChar = party[currentCharacterIndex];
        String classTitle = currentChar.getClass().getSimpleName();
        totalTurnsTaken++; 

        if (currentChar.isPoisoned()) {
            int dmg = 5;
            currentChar.takeDamage(dmg);
            gui.updatePartyBars();
            String msg = currentChar.getName() + " takes " + dmg + " poison damage!";
            gui.showPlayerAction(msg, currentChar.getName(), classTitle);
            gui.setMessage(msg);
            
            currentChar.setPoisoned(currentChar.getPoisonTurns() - 1 > 0, currentChar.getPoisonTurns() - 1);
            if (!currentChar.isPoisoned()) {
                String clearMsg = currentChar.getName() + " is no longer poisoned.";
                gui.showPlayerAction(clearMsg, currentChar.getName(), classTitle);
                gui.setMessage(clearMsg);
            }
            gui.setActiveTurn(currentCharacterIndex, -1); 
        }

        if (currentChar.isStunned()) {
            String msg = currentChar.getName() + " is stunned and skips the turn!";
            gui.showPlayerAction(msg, currentChar.getName(), classTitle);
            gui.setMessage(msg);
            currentChar.setStunned(false);
            nextCharacterTurn();
            return;
        }

        switch (actionType) {
            case "attack" -> {
                Enemy target = chooseEnemy("CHOOSE TARGET TO ATTACK");
                if (target == null) return; 

                String msg;
                if (Math.random() < 0.15) {
                    msg = currentChar.getName() + " swung at " + target.getName() + " but MISSED!";
                } else {
                    int dmg = currentChar.attack();
                    target.takeDamage(dmg);
                    gui.updateEnemyBars();
                    msg = currentChar.getName() + " attacks " + target.getName() + " for " + dmg + " damage!";
                }
                gui.showPlayerAction(msg, currentChar.getName(), classTitle);
                gui.setMessage(msg);
                nextCharacterTurn();
            }

            case "skill" -> {
                String[] skillNames;
                String[] skillFiles;
                int[] mpCosts;
                
                if (currentChar instanceof Warrior) {
                    skillNames = new String[]{"Heavy Strike (10 MP)", "Shield Slam (15 MP)"};
                    skillFiles = new String[]{"heavyStrike.png", "shieldSlam.png"};
                    mpCosts = new int[]{10, 15};
                } else if (currentChar instanceof Mage) {
                    skillNames = new String[]{"Fireball (15 MP)", "Lightning Bolt (20 MP)"};
                    skillFiles = new String[]{"fireball.png", "lightningBolt.png"};
                    mpCosts = new int[]{15, 20};
                } else { 
                    skillNames = new String[]{"Piercing Shot (12 MP)", "Arrow Rain (18 MP)"};
                    skillFiles = new String[]{"piercingShot.png", "arrowRain.png"};
                    mpCosts = new int[]{12, 18};
                }

                JPanel skillPanel = new JPanel();
                skillPanel.setLayout(new BoxLayout(skillPanel, BoxLayout.Y_AXIS));
                skillPanel.setBackground(new Color(42, 45, 62));

                final int[] selectedSkillChoice = {-1}; 

                for (int i = 0; i < skillNames.length; i++) {
                    String targetFile = skillFiles[i];
                    String mainPath = "/com/mycompany/rpg/assets/" + targetFile;
                    String fallbackPath = "/assets/" + targetFile;

                    ImageIcon skillIcon = null;
                    java.io.InputStream is = Main.class.getResourceAsStream(mainPath);
                    if (is == null) {
                        is = Main.class.getResourceAsStream(fallbackPath);
                    }

                    if (is != null) {
                        try {
                            Image rawImage = ImageIO.read(is);
                            is.close();
                            if (rawImage != null) {
                                Image scaledImage = rawImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                                skillIcon = new ImageIcon(scaledImage);
                            }
                        } catch (java.io.IOException ex) {
                            System.out.println("[DEBUG] Failed to load skill icon: " + targetFile);
                        }
                    }

                    JButton btn = new JButton(skillNames[i]);
                    btn.setFont(new Font("Monospaced", Font.BOLD, 13));
                    btn.setBackground(new Color(69, 74, 102));   
                    btn.setForeground(new Color(240, 244, 248)); 
                    btn.setFocusPainted(false);
                    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    btn.setMaximumSize(new Dimension(320, 45));
                    btn.setPreferredSize(new Dimension(320, 45));
                    btn.setBorder(new javax.swing.border.CompoundBorder(
                            new javax.swing.border.LineBorder(new Color(80, 86, 118), 1, true),
                            new javax.swing.border.EmptyBorder(5, 10, 5, 10)
                    ));

                    if (skillIcon != null) {
                        btn.setIcon(skillIcon);
                        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
                        btn.setIconTextGap(10);
                    }

                    final int index = i;
                    btn.addActionListener(clickEvent -> {
                        selectedSkillChoice[0] = index;
                        Window win = SwingUtilities.getWindowAncestor(btn);
                        if (win != null) win.dispose(); 
                    });

                    skillPanel.add(btn);
                    if (i < skillNames.length - 1) {
                        skillPanel.add(Box.createVerticalStrut(8)); 
                    }
                }

                UIManager.put("OptionPane.background", new Color(42, 45, 62));
                UIManager.put("Panel.background", new Color(42, 45, 62));
                UIManager.put("OptionPane.messageForeground", new Color(240, 244, 248));

                JOptionPane.showOptionDialog(
                        gui, skillPanel, "SELECT SKILL TO CAST",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, new Object[]{}, null
                );

                int skillChoice = selectedSkillChoice[0];
                if (skillChoice < 0) return; 

                int chosenMpCost = mpCosts[skillChoice];
                if (currentChar.getMp() < chosenMpCost) {
                    showCustomMessageDialog("INSUFFICIENT MANA", currentChar.getName() + " doesn't have enough MP!");
                    return; 
                }

                Enemy target = chooseEnemy("CHOOSE TARGET FOR SKILL");
                if (target == null) return; 

                currentChar.consumeMp(chosenMpCost);
                int dmg = currentChar.useSkill();
                if (skillChoice == 1) dmg += 8; 
                
                target.takeDamage(dmg);
                gui.updateEnemyBars();
                gui.updatePartyBars();
                
                String msg = currentChar.getName() + " casts " + skillNames[skillChoice].split(" \\(")[0] + " on " + target.getName() + " for " + dmg + " damage!";
                gui.showPlayerAction(msg, currentChar.getName(), classTitle);
                gui.setMessage(msg);

                if (Math.random() < 0.2) {
                    target.setStunned(true);
                    String stunMsg = target.getName() + " was stunned by the impact!";
                    gui.showPlayerAction(stunMsg, currentChar.getName(), classTitle);
                    gui.setMessage(stunMsg);
                }
                nextCharacterTurn();
            }

            case "item" -> {
                if (inventory.isEmpty()) {
                    showCustomMessageDialog("INVENTORY EMPTY", "You do not have any items left in your bag!");
                    return;
                }

                JPanel listPanel = new JPanel();
                listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
                listPanel.setBackground(new Color(42, 45, 62));

                for (int i = 0; i < inventory.size(); i++) {
                    Item item = inventory.get(i);
                    String name = item.getName();
                    
                    String targetFile = "healthPotion.png"; 
                    if (name.contains("Mana") || name.contains("Elixir")) {
                        targetFile = "mpPotion.png";
                    } else if (name.contains("Revive") || name.contains("Scroll")) {
                        targetFile = "reviveScroll.png";
                    }

                    String mainPath = "/com/mycompany/rpg/assets/" + targetFile;
                    String fallbackPath = "/assets/" + targetFile;

                    ImageIcon itemIcon = null;
                    java.io.InputStream is = Main.class.getResourceAsStream(mainPath);
                    if (is == null) {
                        is = Main.class.getResourceAsStream(fallbackPath);
                    }

                    if (is != null) {
                        try {
                            Image rawImage = ImageIO.read(is);
                            is.close();
                            if (rawImage != null) {
                                Image scaledImage = rawImage.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                                itemIcon = new ImageIcon(scaledImage);
                            }
                        } catch (java.io.IOException ex) {
                            System.out.println("[DEBUG] Failed to load item sprite: " + targetFile);
                        }
                    }

                    JButton btn = new JButton(item.getName() + " (" + item.getDescription() + ")");
                    btn.setFont(new Font("Monospaced", Font.BOLD, 13));
                    btn.setBackground(new Color(69, 74, 102));   
                    btn.setForeground(new Color(240, 244, 248)); 
                    btn.setFocusPainted(false);
                    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    btn.setMaximumSize(new Dimension(420, 45));
                    btn.setPreferredSize(new Dimension(420, 45));
                    btn.setBorder(new javax.swing.border.CompoundBorder(
                            new javax.swing.border.LineBorder(new Color(80, 86, 118), 1, true),
                            new javax.swing.border.EmptyBorder(5, 10, 5, 10)
                    ));

                    if (itemIcon != null) {
                        btn.setIcon(itemIcon);
                        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
                        btn.setIconTextGap(12);
                    }

                    final int selectedIndex = i;
                    btn.addActionListener(clickEvent -> {
                        Window win = SwingUtilities.getWindowAncestor(btn);
                        if (win != null) win.dispose();
                        useSelectedBagItem(selectedIndex);
                    });

                    listPanel.add(btn);
                    listPanel.add(Box.createVerticalStrut(6));
                }

                JScrollPane scrollPane = new JScrollPane(listPanel);
                scrollPane.setBorder(null);
                scrollPane.setOpaque(false);
                scrollPane.getViewport().setOpaque(false);
                
                scrollPane.setPreferredSize(new Dimension(440, 190));
                scrollPane.getVerticalScrollBar().setUnitIncrement(12);

                UIManager.put("OptionPane.background", new Color(42, 45, 62));
                UIManager.put("Panel.background", new Color(42, 45, 62));
                UIManager.put("OptionPane.messageForeground", new Color(240, 244, 248));

                JOptionPane.showOptionDialog(
                        gui, scrollPane, "OPEN BAG - SELECT ITEM",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, new Object[]{}, null
                );
            }

            case "flee" -> {
                String fleeText = "Are you sure you want to flee from this encounter?\n(50% Success Rate - Failed escapes inflict 15 splash damage to ALL allies)";
                String[] fleeButtons = {"Attempt Escape", "Stay and Fight"};
                
                int choice = gui.askFleeChoice(fleeText, fleeButtons);
                
                if (choice == 0) { 
                    if (Math.random() < 0.5) {
                        String msg = currentChar.getName() + " managed to escape the battle safely!";
                        gui.showPlayerAction(msg, currentChar.getName(), classTitle);
                        gui.setMessage(msg);
                        showCustomMessageDialog("ESCAPE SUCCESSFUL", "You fled from battle! Progress saved automatically.");
                        executeGameSave();
                        gui.dispose();
                        SwingUtilities.invokeLater(Main::initGameEnvironment);
                    } else {
                        String msg = currentChar.getName() + " tried to flee but couldn't get away!";
                        gui.showPlayerAction(msg, currentChar.getName(), classTitle);
                        gui.setMessage(msg);
                        for (Character ally : party) {
                            ally.takeDamage(15);
                        }
                        nextCharacterTurn(); 
                    }
                }
            }
        }
    }

    private static void useSelectedBagItem(int itemChoice) {
        if (itemChoice < 0 || itemChoice >= inventory.size()) return;
        
        Character currentChar = party[currentCharacterIndex];
        String classTitle = currentChar.getClass().getSimpleName();
        Item selected = inventory.get(itemChoice);
        
        boolean isRevive = selected.getName().contains("Revive") || selected.getName().contains("Scroll");
        if (currentChar.getHp() <= 0 && !isRevive) {
            showCustomMessageDialog("INVALID ACTION", currentChar.getName() + " is fallen and cannot use items right now!");
            return;
        }
        
        if (selected.getName().contains("Revive") || selected.getName().contains("Scroll")) {
            ArrayList<Character> deadAllies = new ArrayList<>();
            for (Character c : party) {
                if (c.getHp() <= 0) {
                    deadAllies.add(c);
                }
            }
            
            if (deadAllies.isEmpty()) {
                showCustomMessageDialog("INVALID ACTION", "All your party members are currently alive! You cannot use a Revive Scroll right now.");
                return;
            }
            
            String[] options = deadAllies.stream()
                    .map(Character::getName)
                    .toArray(String[]::new);
                    
            int choice = gui.askReviveChoice("Select a fallen hero to revive:", options);
            if (choice < 0) return; 
            
            inventory.remove(itemChoice);
            Character targetHero = deadAllies.get(choice);
            targetHero.heal(selected.getEffect()); 
            
            String msg = "✨ " + currentChar.getName() + " uses a Revive Scroll! " + targetHero.getName() + " is resurrected with " + selected.getEffect() + " HP!";
            gui.showPlayerAction(msg, currentChar.getName(), classTitle);
            gui.setMessage(msg);
            nextCharacterTurn();
            return;
        }
        
        inventory.remove(itemChoice); 
        String msg;
        if (selected.getName().contains("Mana") || selected.getName().contains("Elixir")) {
            currentChar.restoreMp(selected.getEffect());
            msg = currentChar.getName() + " drinks " + selected.getName() + " and recovers " + selected.getEffect() + " MP!";
        } else {
            currentChar.heal(selected.getEffect());
            msg = currentChar.getName() + " uses " + selected.getName() + " and restores " + selected.getEffect() + " HP!";
        }
        
        gui.showPlayerAction(msg, currentChar.getName(), classTitle);
        gui.setMessage(msg);
        nextCharacterTurn();
    }

    private static Enemy chooseEnemy(String title) {
        java.util.List<Enemy> aliveEnemies = new java.util.ArrayList<>();
        for (Enemy e : enemies) {
            if (e.getHp() > 0) {
                aliveEnemies.add(e);
            }
        }

        if (aliveEnemies.isEmpty()) return null;

        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setBackground(new Color(42, 45, 62)); 

        final Enemy[] selectedEnemyContainer = {null};

        for (int i = 0; i < aliveEnemies.size(); i++) {
            Enemy enemy = aliveEnemies.get(i);
            String name = enemy.getName();

            String targetFile = "goblin.png"; 
            String lowerName = name.toLowerCase();

            if (lowerName.contains("boss") || lowerName.contains("warlord") || lowerName.contains("troll")) {
                targetFile = "bossTroll.png";
            } else if (lowerName.contains("slime")) {
                targetFile = "slime.png"; 
            } else if (lowerName.contains("skeleton") || lowerName.contains("bone") || lowerName.contains("lich")) {
                targetFile = "skeleton.png";
            } else if (lowerName.contains("vampire") || lowerName.contains("bat")) {
                targetFile = "vampire.png";
            } else if (lowerName.contains("goblin")) {
                targetFile = "goblin.png";
            }

            String mainPath = "/com/mycompany/rpg/assets/" + targetFile;
            String fallbackPath = "/assets/" + targetFile;

            ImageIcon enemyIcon = null;
            java.io.InputStream is = Main.class.getResourceAsStream(mainPath);
            if (is == null) {
                is = Main.class.getResourceAsStream(fallbackPath);
            }

            if (is != null) {
                try {
                    Image rawImage = ImageIO.read(is);
                    is.close();
                    if (rawImage != null) {
                        Image scaledImage = rawImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                        enemyIcon = new ImageIcon(scaledImage);
                    }
                } catch (java.io.IOException ex) {
                    System.out.println("[DEBUG] Failed to load enemy selection icon: " + targetFile);
                }
            }

            String buttonText = enemy.getName() + " (HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + ")";
            if (enemy.isStunned()) {
                buttonText += " [STUNNED]";
            }

            JButton btn = new JButton(buttonText);
            btn.setFont(new Font("Monospaced", Font.BOLD, 13));
            btn.setBackground(new Color(69, 74, 102));   
            btn.setForeground(new Color(240, 244, 248)); 
            btn.setFocusPainted(false);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            btn.setMaximumSize(new Dimension(340, 50));
            btn.setPreferredSize(new Dimension(340, 50));
            btn.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.LineBorder(new Color(80, 86, 118), 1, true),
                    new javax.swing.border.EmptyBorder(5, 10, 5, 10)
                    ));

            if (enemyIcon != null) {
                btn.setIcon(enemyIcon);
                btn.setHorizontalTextPosition(SwingConstants.RIGHT);
                btn.setIconTextGap(14);
            }

            final Enemy targetedEnemy = enemy;
            btn.addActionListener(clickEvent -> {
                selectedEnemyContainer[0] = targetedEnemy;
                Window win = SwingUtilities.getWindowAncestor(btn);
                if (win != null) win.dispose();
            });

            enemyPanel.add(btn);
            if (i < aliveEnemies.size() - 1) {
                enemyPanel.add(Box.createVerticalStrut(8)); 
            }
        }

        UIManager.put("OptionPane.background", new Color(42, 45, 62));
        UIManager.put("Panel.background", new Color(42, 45, 62));
        UIManager.put("OptionPane.messageForeground", new Color(240, 244, 248));

        JOptionPane.showOptionDialog(
                gui, enemyPanel, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
                );

        return selectedEnemyContainer[0];
    }

    private static boolean checkWaveClearCondition() {
        boolean allEnemiesDead = true;
        for (Enemy e : enemies) {
            if (e.getHp() > 0) {
                allEnemiesDead = false;
                break;
            }
        }
        
        if (allEnemiesDead) {
            checkWaveEnd();
            return true;
        }
        return false;
    }

    private static void nextCharacterTurn() {
        if (checkWaveClearCondition()) {
            return; 
        }

        if (checkPartyDefeated()) {
            triggerGameOver(false);
            return;
        }

        currentCharacterIndex++;
        if (currentCharacterIndex >= party.length) {
            currentCharacterIndex = 0;
            enemyTurn();
            
            if (party[currentCharacterIndex].getHp() <= 0) {
                nextCharacterTurn(); 
                return;
            }
            gui.setMessage("It's " + party[currentCharacterIndex].getName() + "'s turn!");
            gui.setActiveTurn(currentCharacterIndex, -1);
        } else {
            Character c = party[currentCharacterIndex];
            if (c.getHp() <= 0) { 
                nextCharacterTurn();
                return;
            }
            gui.setMessage("It's " + c.getName() + "'s turn!");
            gui.setActiveTurn(currentCharacterIndex, -1);
        }
    }

    private static void enemyTurn() {
        for (int i = 0; i < enemies.length; i++) {
            Enemy e = enemies[i];
            if (e.getHp() <= 0) continue;
            
            if (checkPartyDefeated()) {
                triggerGameOver(false);
                return; 
            }

            ArrayList<Character> validTargets = new ArrayList<>();
            for (Character c : party) if (c.getHp() > 0) validTargets.add(c);
            if (validTargets.isEmpty()) return;
            Character singleTarget = validTargets.get((int) (Math.random() * validTargets.size()));

            double actionRoll = Math.random();
            String currentActionMsg = "";

            gui.setActiveTurn(-1, i);

            if (e instanceof Troll) {
                if (actionRoll < 0.45) {
                    int dmg = e.attack();
                    singleTarget.takeDamage(dmg);
                    currentActionMsg = "⚡ THE WARLORD slams " + singleTarget.getName() + " with an iron club for " + dmg + " damage!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                } else if (actionRoll < 0.70) {
                    e.setAttackBuffed(true);
                    e.heal(35); 
                    currentActionMsg = "⚡ THE WARLORD roars into the heavens! Recovers 35 HP and charges his next attack!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                } else if (actionRoll < 0.85) {
                    currentActionMsg = "⚡ THE WARLORD strikes the floor! Tremors sap mental focus.";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                    
                    for (Character ally : validTargets) {
                        ally.consumeMp(12);
                    }
                    
                    currentActionMsg = "All active heroes lost 12 MP to the boss's intimidation!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                } else {
                    int baseDmg = e.attack() - 5; 
                    currentActionMsg = "🔥 WARNING: THE WARLORD UNLEASHES AN APOCALYPTIC SQUALL CLEAVE! 🔥";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                    
                    for (Character ally : validTargets) {
                        ally.takeDamage(baseDmg);
                        if (Math.random() < 0.3) ally.setPoisoned(true, 2); 
                    }
                    
                    currentActionMsg = "The entire party takes " + baseDmg + " splash damage from the shockwave!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                }
            } else {
                if (actionRoll < 0.60) {
                    int dmg = e.attack();
                    singleTarget.takeDamage(dmg);
                    currentActionMsg = e.getName() + " attacks " + singleTarget.getName() + " for " + dmg + " damage!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                } else if (actionRoll < 0.85) {
                    e.setAttackBuffed(true);
                    e.heal(10);
                    currentActionMsg = e.getName() + " focuses power! Offense boosted and recovered 10 HP!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                } else {
                    singleTarget.consumeMp(8);
                    currentActionMsg = e.getName() + " unleashes an intimidating Taunt! " + singleTarget.getName() + " lost 8 MP!";
                    gui.showEnemyAction(currentActionMsg, e.getName());
                    gui.setMessage(currentActionMsg);
                }
            }
            
            gui.updatePartyBars();
            gui.updateEnemyBars();
        }

        if (checkPartyDefeated()) {
            triggerGameOver(false);
            return;
        }

        checkWaveEnd();
    }

    private static void checkWaveEnd() {
        boolean allDead = true;
        for (Enemy e : enemies) {
            if (e.getHp() > 0) {
                allDead = false;
            } else {
                totalEnemiesDefeated++;
            }
        }

        if (allDead) {
            int roundBonus = Arrays.stream(enemies).mapToInt(e -> 10).sum();
            coins += roundBonus; 
            gui.updateCoinCount(coins);
            gui.setMessage("Wave " + (currentWave + 1) + " cleared! Earned " + roundBonus + " coins.");

            currentWave++;
            // ===== FIXED: DISPATCHES ENEMY LAYER PROPERTIES ON WAVE CLEAR CHECKS =====
            FileHandler.saveGame(party, currentWave, coins, inventory, enemies); 

           if (currentWave < 4) { 
                coins = Shop.openShop(gui, inventory, coins);
                FileHandler.saveGame(party, currentWave, coins, inventory, enemies); 

                showWaveStartBreakPrompt();
                
                enemies = createWave(currentWave);
                gui.setEnemies(enemies);
                
                currentCharacterIndex = 0;
                gui.setMessage("Wave " + (currentWave + 1) + " begins! " + party[currentCharacterIndex].getName() + "'s turn");
                gui.setActiveTurn(currentCharacterIndex, -1);
            } else {
                triggerGameOver(true); 
            }
        }
    }

    private static boolean checkPartyDefeated() {
        return Arrays.stream(party).allMatch(c -> c.getHp() <= 0);
    }

    private static Enemy[] createWave(int wave) {
        return switch (wave) {
            case 0 -> new Enemy[]{ 
                new Slime("Acid Slime", 40, 8), 
                new Goblin("Goblin Scout", 45, 10), 
                new Goblin("Goblin Archer", 45, 10),
                new Slime("Toxic Slime", 40, 8)
            };
            case 1 -> new Enemy[]{ 
                new Skeleton("Bone Grunt", 65, 12), 
                new Orc("Orc Marauder", 80, 15), 
                new Skeleton("Bone Mage", 60, 14),
                new Goblin("Goblin Assassin", 55, 16)
            };
            case 2 -> new Enemy[]{ 
                new Vampire("Fledgling Bat", 85, 16), 
                new Orc("Orc Warchief", 100, 18), 
                new Vampire("Vampire Noble", 90, 20),
                new Skeleton("Lich Vanguard", 80, 15)
            };
            default -> new Enemy[]{ 
                new Troll("⚡ TROLL WARLORD BOSS ⚡", 400, 25) 
            };
        };
    }

    private static void triggerGameOver(boolean victory) {
        if (gui != null) gui.dispose();

        Color COLOR_BG_PANEL     = new Color(42, 45, 62);   
        Color COLOR_BG_CARD      = new Color(52, 56, 77);   
        Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248); 
        Color COLOR_TEXT_MUTED   = new Color(174, 190, 209); 
        Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105); 
        Color COLOR_BORDER       = new Color(80, 86, 118);  
        Color COLOR_RED_BANNER   = new Color(235, 94, 85);

        JPanel endScreenPanel = new JPanel(new BorderLayout(0, 14));
        endScreenPanel.setBackground(COLOR_BG_PANEL);
        endScreenPanel.setPreferredSize(new Dimension(440, 280));
        endScreenPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        String headerTitleText = victory ? "  SUCCESSFUL VICTORY  " : " DEFEAT - PARTY WIPED ";
        JLabel outcomeBannerLabel = new JLabel(headerTitleText, SwingConstants.CENTER);
        outcomeBannerLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        outcomeBannerLabel.setForeground(victory ? COLOR_ACCENT_GOLD : COLOR_RED_BANNER);
        endScreenPanel.add(outcomeBannerLabel, BorderLayout.NORTH);

        JPanel statsContainer = new JPanel(new GridLayout(4, 1, 0, 6));
        statsContainer.setBackground(COLOR_BG_CARD);
        statsContainer.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 20, 15, 20)
                ));

        JLabel waveMetric = new JLabel(String.format("Waves Cleared:        %d / 4", currentWave));
        JLabel turnMetric = new JLabel(String.format("Total Combat Turns:   %d Turns", totalTurnsTaken));
        
        JPanel goldRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        goldRow.setOpaque(false);
        JLabel goldLeftText = new JLabel("Accumulated Savings:  ");
        goldLeftText.setFont(new Font("Monospaced", Font.BOLD, 13));
        goldLeftText.setForeground(COLOR_TEXT_LIGHT);
        
        ImageIcon coinGraphic = loadInternalAssetStreamIcon("coin.png", 16, 16);
        JLabel coinImgLabel = (coinGraphic != null) ? new JLabel(coinGraphic) : new JLabel("🪙");
        
        JLabel goldRightText = new JLabel(" " + coins + " Coins");
        goldRightText.setFont(new Font("Monospaced", Font.BOLD, 13));
        goldRightText.setForeground(COLOR_ACCENT_GOLD);
        goldRow.add(goldLeftText);
        goldRow.add(coinImgLabel);
        goldRow.add(goldRightText);

        JLabel promptText = new JLabel("Click below to return to the Main Menu.", SwingConstants.CENTER);
        promptText.setFont(new Font("Monospaced", Font.ITALIC, 12));
        promptText.setForeground(COLOR_TEXT_MUTED);

        for (JLabel lbl : new JLabel[]{waveMetric, turnMetric}) {
            lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
            lbl.setForeground(COLOR_TEXT_LIGHT);
            statsContainer.add(lbl);
        }
        statsContainer.add(goldRow);
        statsContainer.add(promptText);
        endScreenPanel.add(statsContainer, BorderLayout.CENTER);

        JButton returnMenuBtn = new JButton("RETURN TO MAIN MENU");
        returnMenuBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        returnMenuBtn.setBackground(new Color(69, 74, 102)); 
        returnMenuBtn.setForeground(COLOR_TEXT_LIGHT);
        returnMenuBtn.setFocusPainted(false);
        returnMenuBtn.setPreferredSize(new Dimension(0, 42));
        returnMenuBtn.setBorder(new LineBorder(COLOR_BORDER, 1, true));

        returnMenuBtn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(returnMenuBtn);
            if (win != null) win.dispose();
        });
        endScreenPanel.add(returnMenuBtn, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);

        JOptionPane.showOptionDialog(
                null, 
                endScreenPanel, 
                victory ? "VICTORY REPORT" : "DEFEAT SUMMARY",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.PLAIN_MESSAGE,
                null, 
                new Object[]{}, 
                null
        );

        SwingUtilities.invokeLater(Main::initGameEnvironment);
    }

    private static void showCustomMessageDialog(String titleHeader, String messageText) {
        if (gui != null) {
            gui.showThemedNotification(titleHeader, messageText);
        } else {
            UIManager.put("OptionPane.background", new Color(42, 45, 62));
            UIManager.put("Panel.background", new Color(42, 45, 62));
            UIManager.put("OptionPane.messageForeground", new Color(240, 244, 248));
            JOptionPane.showMessageDialog(null, messageText, titleHeader, JOptionPane.PLAIN_MESSAGE);
        }
    }

    private static int showCustomConfirmDialog(String titleHeader, String messageBody) {
        if (gui != null) {
            String[] choices = {"Attempt Escape", "Stay and Fight"};
            return gui.showThemedConfirmDialog(titleHeader, messageBody, choices);
        } else {
            UIManager.put("OptionPane.background", new Color(42, 45, 62));
            UIManager.put("Panel.background", new Color(42, 45, 62));
            UIManager.put("OptionPane.messageForeground", new Color(240, 244, 248));
            String[] choices = {"Attempt Escape", "Stay and Fight"};
            return JOptionPane.showOptionDialog(
                    null, messageBody, titleHeader,
                    JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, choices, choices[0]
            );
        }
    }
}