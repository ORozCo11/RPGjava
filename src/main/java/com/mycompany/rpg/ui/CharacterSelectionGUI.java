package com.mycompany.rpg.ui;

import com.mycompany.rpg.characters.Character;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;

public class CharacterSelectionGUI extends JFrame {

    // Sharing your exact modern dark RPG color profile
    private final Color COLOR_BG_MAIN      = new Color(30, 32, 44);
    private final Color COLOR_BG_PANEL     = new Color(42, 45, 62);
    private final Color COLOR_BG_CARD      = new Color(52, 56, 77);
    private final Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248);
    private final Color COLOR_TEXT_MUTED   = new Color(174, 190, 209);
    private final Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105);
    private final Color COLOR_BORDER       = new Color(80, 86, 118);
    private final Color COLOR_HP           = new Color(235, 94, 85);
    private final Color COLOR_MP           = new Color(83, 144, 217);

    private final Character[] availableCharacters;
    private final ArrayList<Character> selectedParty = new ArrayList<>();
    private final JLabel instructionLabel;
    private final SelectionCallback callback;

    // Interface functional contract callback
    public interface SelectionCallback {
        void onPartySelected(Character[] chosenParty);
    }

    public CharacterSelectionGUI(Character[] available, SelectionCallback callback) {
        this.availableCharacters = available;
        this.callback = callback;

        setTitle("RPG Legends - Assemble Party");
        // Expanded layout boundaries slightly to perfectly accommodate images + stats frames
        setSize(880, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG_MAIN);
        setLayout(new BorderLayout(15, 15));

        // Window Padding Wrapper
        JPanel mainWrapper = new JPanel(new BorderLayout(15, 15));
        mainWrapper.setBackground(COLOR_BG_MAIN);
        mainWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        // ===== Top Header Banner =====
        instructionLabel = new JLabel("CHOOSE YOUR FIRST HERO", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        instructionLabel.setForeground(COLOR_ACCENT_GOLD);
        instructionLabel.setOpaque(true);
        instructionLabel.setBackground(COLOR_BG_PANEL);
        instructionLabel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        mainWrapper.add(instructionLabel, BorderLayout.NORTH);

        // ===== Center Cards Deck =====
        JPanel cardsContainer = new JPanel(new GridLayout(1, available.length, 15, 0));
        cardsContainer.setBackground(COLOR_BG_MAIN);

        for (int i = 0; i < available.length; i++) {
            Character c = available[i];
            int index = i;

            JPanel card = new JPanel(new BorderLayout(10, 10));
            card.setBackground(COLOR_BG_PANEL);
            card.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            // Name Block
            JLabel nameLabel = new JLabel(c.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
            nameLabel.setForeground(COLOR_TEXT_LIGHT);

            // Sub-Class Tag
            String classTitle = c.getClass().getSimpleName();
            JLabel classLabel = new JLabel("[" + classTitle + "]", SwingConstants.CENTER);
            classLabel.setFont(new Font("Monospaced", Font.BOLD | Font.ITALIC, 13));
            classLabel.setForeground(COLOR_TEXT_MUTED);

            // ===== SMART PORTRAIT RESOLVER: THOR VS FREYA =====
            String imageFileName = classTitle.toLowerCase() + ".png";
            if (c.getName() != null && c.getName().equalsIgnoreCase("Freya")) {
                imageFileName = "womanwarrior.png";
            }

            String mainPath = "/com/mycompany/rpg/assets/" + imageFileName;
            String fallbackPath = "/assets/" + imageFileName;

            ImageIcon portraitIcon = createScaledIcon(mainPath, 64, 64);
            if (portraitIcon == null) {
                portraitIcon = createScaledIcon(fallbackPath, 64, 64);
            }

            JLabel portraitLabel;
            if (portraitIcon != null) {
                portraitLabel = new JLabel(portraitIcon, SwingConstants.CENTER);
            } else {
                portraitLabel = new JLabel("[ " + classTitle.substring(0, 1) + " ]", SwingConstants.CENTER);
                portraitLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                portraitLabel.setForeground(COLOR_ACCENT_GOLD);
            }
            portraitLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

            // Assemble Identity Header Stack Panel
            JPanel identityPanel = new JPanel(new BorderLayout(0, 4));
            identityPanel.setOpaque(false);
            identityPanel.add(nameLabel, BorderLayout.NORTH);
            identityPanel.add(classLabel, BorderLayout.CENTER);
            identityPanel.add(portraitLabel, BorderLayout.SOUTH);
            card.add(identityPanel, BorderLayout.NORTH);

            // Stats Sub-Layout Box
            JPanel statsPanel = new JPanel(new GridLayout(4, 1, 5, 2));
            statsPanel.setBackground(COLOR_BG_CARD);
            statsPanel.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER, 1),
                    new EmptyBorder(10, 10, 10, 10)
            ));

            statsPanel.add(createStatLabel("HP: " + c.getMaxHp(), COLOR_HP));
            statsPanel.add(createStatLabel("MP: " + c.getMaxMp(), COLOR_MP));
            statsPanel.add(createStatLabel("ATK: " + c.getAttackPower(), COLOR_TEXT_LIGHT));
            statsPanel.add(createStatLabel("DEF: " + c.getDefensePower(), COLOR_TEXT_MUTED));
            card.add(statsPanel, BorderLayout.CENTER);

            // Recruit Control Input
            JButton selectBtn = new JButton("RECRUIT");
            selectBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
            selectBtn.setBackground(COLOR_BORDER);
            selectBtn.setForeground(COLOR_TEXT_LIGHT);
            selectBtn.setFocusPainted(false);
            selectBtn.setBorder(new LineBorder(COLOR_BORDER.brighter(), 1, true));

            selectBtn.addActionListener(e -> handleSelection(index, selectBtn, card));
            card.add(selectBtn, BorderLayout.SOUTH);

            cardsContainer.add(card);
        }

        mainWrapper.add(cardsContainer, BorderLayout.CENTER);
        add(mainWrapper);
        setVisible(true);
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(color);
        return lbl;
    }

    private void handleSelection(int index, JButton btn, JPanel card) {
        Character selected = availableCharacters[index];
        
        // Prevent double selecting same exact character instance
        if (selectedParty.contains(selected)) return;

        selectedParty.add(selected);
        btn.setText("SELECTED");
        btn.setEnabled(false);
        card.setBorder(new LineBorder(COLOR_ACCENT_GOLD, 2, true));

        if (selectedParty.size() == 1) {
            instructionLabel.setText("CHOOSE YOUR SECOND HERO");
        } else if (selectedParty.size() == 2) {
            // Party choice satisfied! Wrap up step loop execution
            Timer timer = new Timer(500, e -> {
                this.dispose();
                callback.onPartySelected(selectedParty.toArray(new Character[0]));
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    // ===== STREAM RESOURCE PORTRAIT PARSER HOOK =====
    private static ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            InputStream is = CharacterSelectionGUI.class.getResourceAsStream(path);
            if (is == null) return null;
            Image rawImage = ImageIO.read(is);
            is.close();
            if (rawImage == null) return null;

            Image scaledImage = rawImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }
}