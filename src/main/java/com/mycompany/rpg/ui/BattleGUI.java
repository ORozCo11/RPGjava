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

    public BattleGUI(Character[] party, Enemy[] enemies) {
        this.party = party;
        this.enemies = enemies;

        setTitle("RPG Battle");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        Font titleFont = new Font("Monospaced", Font.BOLD, 20);
        Font labelFont = new Font("Monospaced", Font.BOLD, 16);
        Font normalFont = new Font("Monospaced", Font.PLAIN, 14);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ===== Title =====
        JLabel titleLabel = new JLabel("RPG Battle", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== Center Section =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // ===== Enemy Area =====
        enemyPanel = new JPanel();
        enemyPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(Color.BLACK, 1), "Enemies"),
                new EmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== Message Area =====
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(Color.BLACK, 1), "Battle Log"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setFont(labelFont);
        messagePane.setBackground(new Color(245, 245, 245));

        StyledDocument doc = messagePane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JScrollPane messageScroll = new JScrollPane(messagePane);
        messageScroll.setPreferredSize(new Dimension(800, 180));
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        // ===== Party Area =====
        partyPanel = new JPanel();
        partyPanel.setBorder(new CompoundBorder(
                new TitledBorder(new LineBorder(Color.BLACK, 1), "Party"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        centerPanel.add(enemyPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(messagePanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(partyPanel);

        // ===== Button Area =====
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        attackBtn = new JButton("Fight");
        skillBtn = new JButton("Skill");
        itemBtn = new JButton("Bag");
        fleeBtn = new JButton("Run");

        attackBtn.setFont(labelFont);
        skillBtn.setFont(labelFont);
        itemBtn.setFont(labelFont);
        fleeBtn.setFont(labelFont);

        buttonPanel.add(attackBtn);
        buttonPanel.add(skillBtn);
        buttonPanel.add(itemBtn);
        buttonPanel.add(fleeBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        refreshEnemies();
        refreshParty();

        setVisible(true);
    }

    // =========================================================
    // REFRESH GUI
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
        enemyPanel.setLayout(new GridLayout(1, enemyCount, 20, 10));

        if (enemies != null) {
            for (Enemy e : enemies) {
                JPanel card = new JPanel(new BorderLayout(5, 5));
                card.setBorder(new CompoundBorder(
                        new LineBorder(Color.BLACK, 1),
                        new EmptyBorder(10, 10, 10, 10)
                ));

                JLabel nameLabel = new JLabel("[ " + e.getName() + " ]", SwingConstants.CENTER);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, 15));

                JProgressBar hpBar = new JProgressBar(0, e.getMaxHp());
                hpBar.setValue(e.getHp());
                hpBar.setString(e.getHp() + "/" + e.getMaxHp());
                hpBar.setStringPainted(true);

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
        partyPanel.setLayout(new GridLayout(1, partyCount, 20, 10));

        if (party != null) {
            for (Character c : party) {
                JPanel card = new JPanel(new BorderLayout(5, 5));
                card.setBorder(new CompoundBorder(
                        new LineBorder(Color.BLACK, 1),
                        new EmptyBorder(10, 10, 10, 10)
                ));

                JLabel nameLabel = new JLabel("[ " + c.getName() + " (" + c.getClass().getSimpleName() + ") ]", SwingConstants.CENTER);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, 15));

                JPanel barsPanel = new JPanel(new GridLayout(4, 1, 5, 5));

                JLabel hpLabel = new JLabel("HP:", SwingConstants.LEFT);
                hpLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
                JProgressBar hpBar = new JProgressBar(0, c.getMaxHp());
                hpBar.setValue(c.getHp());
                hpBar.setString(c.getHp() + "/" + c.getMaxHp());
                hpBar.setStringPainted(true);

                JLabel mpLabel = new JLabel("MP:", SwingConstants.LEFT);
                mpLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
                JProgressBar mpBar = new JProgressBar(0, c.getMaxMp());
                mpBar.setValue(c.getMp());
                mpBar.setString(c.getMp() + "/" + c.getMaxMp());
                mpBar.setStringPainted(true);

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
    // COMPATIBILITY METHODS FOR YOUR MAIN.JAVA
    // =========================================================
    public void setPlayerHp(int hp) {
        refreshParty();
    }

    public void setEnemyHp(int hp) {
        refreshEnemies();
    }

    public void updatePartyBars() {
        refreshParty();
    }

    public void updateEnemyBars() {
        refreshEnemies();
    }

    // =========================================================
    // MESSAGE SYSTEM
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
    public void onAttack(ActionListener listener) {
        attackBtn.addActionListener(listener);
    }

    public void onSkill(ActionListener listener) {
        skillBtn.addActionListener(listener);
    }

    public void onItem(ActionListener listener) {
        itemBtn.addActionListener(listener);
    }

    public void onFlee(ActionListener listener) {
        fleeBtn.addActionListener(listener);
    }

    // =========================================================
    // DIALOG CHOICES
    // =========================================================
    public int askEnemyChoice(String message, String[] options) {
        return JOptionPane.showOptionDialog(
                this,
                message,
                "Choose Enemy",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    public int askItemChoice(String message, String[] options) {
        return JOptionPane.showOptionDialog(
                this,
                message,
                "Choose Item",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );
    }
}