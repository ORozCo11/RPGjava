package com.mycompany.rpg.ui;

import com.mycompany.rpg.characters.Character;
import com.mycompany.rpg.enemies.Enemy;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

public class BattleGUI extends JFrame {

    private Character[] party;
    private Enemy[] enemies;

    private JPanel enemyPanel;
    private JPanel partyPanel;
    private JTextPane messagePane;
    private JButton attackBtn, skillBtn, itemBtn, fleeBtn;

    private final java.util.List<JProgressBar> enemyHpBars = new ArrayList<>();
    private final java.util.List<JLabel> enemyLabels = new ArrayList<>();

    private final java.util.List<JProgressBar> partyHpBars = new ArrayList<>();
    private final java.util.List<JProgressBar> partyMpBars = new ArrayList<>();
    private final java.util.List<JLabel> partyLabels = new ArrayList<>();

    private final LinkedList<String> messageLines = new LinkedList<>();

    // =========================================================
    // COLOR PALETTE CONFIGURATION (Modern Dark-Mode RPG Theme)
    // =========================================================
    private final Color COLOR_BG_MAIN      = new Color(30, 32, 44);   // Deep Navy Slate
    private final Color COLOR_BG_PANEL     = new Color(42, 45, 62);   // Lighter Slate for containers
    private final Color COLOR_BG_CARD      = new Color(52, 56, 77);   // Dark Card Background
    private final Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248); // Crisp White/Blue Text
    private final Color COLOR_TEXT_MUTED   = new Color(174, 190, 209); // Silvery Gray Text
    private final Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105); // Fantasy Gold
    
    // Bar Colors
    private final Color COLOR_HP           = new Color(235, 94, 85);   // Vivid Crimson
    private final Color COLOR_MP           = new Color(83, 144, 217);  // Mystic Mana Blue
    private final Color COLOR_BAR_BG       = new Color(24, 26, 36);    // Sunken Black for Bar BG
    
    // Buttons
    private final Color COLOR_BTN_DEFAULT  = new Color(69, 74, 102);  // Muted Indigo Button
    private final Color COLOR_BORDER       = new Color(80, 86, 118);  // Clean inner border line

    public BattleGUI(Character[] party, Enemy[] enemies) {
        this.party = party;
        this.enemies = enemies;

        setTitle("RPG Battle System");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG_MAIN);

        // Global Fonts
        Font titleFont = new Font("Monospaced", Font.BOLD, 22);
        Font labelFont = new Font("Monospaced", Font.BOLD, 15);

        // Core Wrapper
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(COLOR_BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // ===== Title =====
        JLabel titleLabel = new JLabel("⚔ BATTLE ENCOUNTER ⚔", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(COLOR_ACCENT_GOLD);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(COLOR_BG_PANEL);
        titleLabel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 2, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== Center Stack Container =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(COLOR_BG_MAIN);

        // ===== Enemy Area =====
        enemyPanel = new JPanel();
        enemyPanel.setBackground(COLOR_BG_PANEL);
        TitledBorder enemyTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " ENEMIES ", TitledBorder.LEFT, TitledBorder.TOP, labelFont, COLOR_HP);
        enemyPanel.setBorder(new CompoundBorder(enemyTitle, new EmptyBorder(12, 12, 12, 12)));

        // ===== Message Area =====
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(COLOR_BG_PANEL);
        TitledBorder logTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " BATTLE LOG ", TitledBorder.LEFT, TitledBorder.TOP, labelFont, COLOR_ACCENT_GOLD);
        messagePanel.setBorder(new CompoundBorder(logTitle, new EmptyBorder(12, 12, 12, 12)));

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setFont(new Font("Monospaced", Font.PLAIN, 15));
        messagePane.setBackground(COLOR_BAR_BG);
        messagePane.setForeground(COLOR_TEXT_LIGHT);

        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JScrollPane messageScroll = new JScrollPane(messagePane);
        messageScroll.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        messageScroll.setPreferredSize(new Dimension(800, 160));
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        // ===== Party Area =====
        partyPanel = new JPanel();
        partyPanel.setBackground(COLOR_BG_PANEL);
        TitledBorder partyTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " YOUR PARTY ", TitledBorder.LEFT, TitledBorder.TOP, labelFont, COLOR_MP);
        partyPanel.setBorder(new CompoundBorder(partyTitle, new EmptyBorder(12, 12, 12, 12)));

        // Assemble Stack
        centerPanel.add(enemyPanel);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(messagePanel);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(partyPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== Button Navigation Area =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setBackground(COLOR_BG_MAIN);
        buttonPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        attackBtn = createStyledButton("FIGHT (A)", labelFont);
        skillBtn = createStyledButton("SKILL (S)", labelFont);
        itemBtn = createStyledButton("BAG (I)", labelFont);
        fleeBtn = createStyledButton("RUN (R)", labelFont);

        buttonPanel.add(attackBtn);
        buttonPanel.add(skillBtn);
        buttonPanel.add(itemBtn);
        buttonPanel.add(fleeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // Initial Data Populate
        refreshEnemies();
        refreshParty();

        setVisible(true);
    }

    // Helper method to uniformly style modern UI control inputs
    private JButton createStyledButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(COLOR_BTN_DEFAULT);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new LineBorder(COLOR_BORDER, 2, true));
        
        // Dynamic Hover Feedback
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BTN_DEFAULT.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BTN_DEFAULT);
            }
        });
        return btn;
    }

    // Helper method to uniformly style status meters
    private void customizeProgressBar(JProgressBar bar, Color fillStyle) {
        bar.setOpaque(true);
        bar.setBackground(COLOR_BAR_BG);
        bar.setForeground(fillStyle);
        bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() { return COLOR_TEXT_LIGHT; }
            @Override
            protected Color getSelectionForeground() { return COLOR_TEXT_LIGHT; }
        });
        bar.setBorder(new LineBorder(COLOR_BG_MAIN, 1));
    }

    // =========================================================
    // REFRESH GUI COMPONENTS
    // =========================================================
    public void setEnemies(Enemy[] enemies) {
        this.enemies = enemies;
        refreshEnemies();
    }

    public void setParty(Character[] party) {
        this.party = party;
        refreshParty();
    }

    public void refreshEnemies() {
        enemyPanel.removeAll();
        enemyHpBars.clear();
        enemyLabels.clear();

        int enemyCount = (enemies == null || enemies.length == 0) ? 1 : enemies.length;
        enemyPanel.setLayout(new GridLayout(1, enemyCount, 20, 0));

        if (enemies != null) {
            for (Enemy e : enemies) {
                JPanel card = new JPanel(new BorderLayout(5, 8));
                card.setBackground(COLOR_BG_CARD);
                card.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_BORDER, 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                ));

                JLabel nameLabel = new JLabel(e.getName(), SwingConstants.CENTER);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                nameLabel.setForeground(COLOR_TEXT_LIGHT);

                JProgressBar hpBar = new JProgressBar(0, e.getMaxHp());
                hpBar.setValue(e.getHp());
                hpBar.setString(e.getHp() + " / " + e.getMaxHp() + " HP");
                hpBar.setStringPainted(true);
                hpBar.setFont(new Font("Monospaced", Font.BOLD, 12));
                customizeProgressBar(hpBar, COLOR_HP);

                card.add(nameLabel, BorderLayout.NORTH);
                card.add(hpBar, BorderLayout.CENTER);

                enemyLabels.add(nameLabel);
                enemyHpBars.add(hpBar);
                enemyPanel.add(card);
            }
        }
        enemyPanel.revalidate();
        enemyPanel.repaint();
    }

    public void refreshParty() {
        partyPanel.removeAll();
        partyHpBars.clear();
        partyMpBars.clear();
        partyLabels.clear();

        int partyCount = (party == null || party.length == 0) ? 1 : party.length;
        partyPanel.setLayout(new GridLayout(1, partyCount, 20, 0));

        if (party != null) {
            for (Character c : party) {
                JPanel card = new JPanel(new BorderLayout(5, 8));
                card.setBackground(COLOR_BG_CARD);
                card.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_BORDER, 1, true),
                        new EmptyBorder(12, 12, 12, 12)
                ));

                String classTitle = c.getClass().getSimpleName();
                JLabel nameLabel = new JLabel(c.getName() + " (" + classTitle + ")", SwingConstants.CENTER);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
                nameLabel.setForeground(COLOR_ACCENT_GOLD);

                JPanel barsPanel = new JPanel(new GridLayout(4, 1, 2, 2));
                barsPanel.setBackground(COLOR_BG_CARD);

                JLabel hpLabel = new JLabel(" HP Status:", SwingConstants.LEFT);
                hpLabel.setForeground(COLOR_TEXT_MUTED);
                hpLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
                
                JProgressBar hpBar = new JProgressBar(0, c.getMaxHp());
                hpBar.setValue(c.getHp());
                hpBar.setString(c.getHp() + " / " + c.getMaxHp());
                hpBar.setStringPainted(true);
                hpBar.setFont(new Font("Monospaced", Font.BOLD, 12));
                customizeProgressBar(hpBar, COLOR_HP);

                JLabel mpLabel = new JLabel(" Mana Pool:", SwingConstants.LEFT);
                mpLabel.setForeground(COLOR_TEXT_MUTED);
                mpLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
                
                JProgressBar mpBar = new JProgressBar(0, c.getMaxMp());
                mpBar.setValue(c.getMp());
                mpBar.setString(c.getMp() + " / " + c.getMaxMp());
                mpBar.setStringPainted(true);
                mpBar.setFont(new Font("Monospaced", Font.BOLD, 12));
                customizeProgressBar(mpBar, COLOR_MP);

                barsPanel.add(hpLabel);
                barsPanel.add(hpBar);
                barsPanel.add(mpLabel);
                barsPanel.add(mpBar);

                card.add(nameLabel, BorderLayout.NORTH);
                card.add(barsPanel, BorderLayout.CENTER);

                partyLabels.add(nameLabel);
                partyHpBars.add(hpBar);
                partyMpBars.add(mpBar);
                partyPanel.add(card);
            }
        }
        partyPanel.revalidate();
        partyPanel.repaint();
    }

    // =========================================================
    // COMPATIBILITY METHODS
    // =========================================================
    public void setPlayerHp(int hp) { refreshParty(); }
    public void setEnemyHp(int hp) { refreshEnemies(); }
    public void updatePartyBars() { refreshParty(); }
    public void updateEnemyBars() { refreshEnemies(); }

    // =========================================================
    // MESSAGE SYSTEM (Keeps up to last 6 events cleanly displayed)
    // =========================================================
    public void setMessage(String msg) {
        if (messageLines.size() >= 6) {
            messageLines.removeFirst();
        }
        messageLines.add(msg);

        StringBuilder sb = new StringBuilder();
        for (String line : messageLines) {
            sb.append(line).append("\n");
        }
        messagePane.setText(sb.toString());

        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    // =========================================================
    // BUTTON LISTENERS
    // =========================================================
    public void onAttack(ActionListener listener) { attackBtn.addActionListener(listener); }
    public void onSkill(ActionListener listener) { skillBtn.addActionListener(listener); }
    public void onItem(ActionListener listener) { itemBtn.addActionListener(listener); }
    public void onFlee(ActionListener listener) { fleeBtn.addActionListener(listener); }

    // =========================================================
    // DIALOG CHOICES
    // =========================================================
    public int askEnemyChoice(String message, String[] options) {
        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_LIGHT);
        return JOptionPane.showOptionDialog(
                this, message, "Target Track",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
    }

    public int askItemChoice(String message, String[] options) {
        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_LIGHT);
        return JOptionPane.showOptionDialog(
                this, message, "Inventory",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]
        );
    }
}