package com.mycompany.rpg.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattleGUI extends JFrame {

    private JLabel messageLabel;
    private JButton attackBtn, skillBtn, itemBtn, fleeBtn;
    private JProgressBar playerHpBar, enemyHpBar;
    private JLabel playerLabel, enemyLabel;

    public BattleGUI(String playerName, int playerHp, String enemyName, int enemyHp) {
        setTitle("RPG Battle");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Top panel for enemy =====
        JPanel enemyPanel = new JPanel(new BorderLayout());
        enemyLabel = new JLabel(enemyName, SwingConstants.CENTER);
        enemyHpBar = new JProgressBar(0, enemyHp);
        enemyHpBar.setValue(enemyHp);
        enemyHpBar.setStringPainted(true);
        enemyPanel.add(enemyLabel, BorderLayout.CENTER);
        enemyPanel.add(enemyHpBar, BorderLayout.SOUTH);

        // ===== Center panel for messages =====
        JPanel messagePanel = new JPanel();
        messageLabel = new JLabel("What will " + playerName + " do?");
        messagePanel.add(messageLabel);

        // ===== Bottom panel for player and buttons =====
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerLabel = new JLabel(playerName, SwingConstants.CENTER);
        playerHpBar = new JProgressBar(0, playerHp);
        playerHpBar.setValue(playerHp);
        playerHpBar.setStringPainted(true);

        JPanel buttonPanel = new JPanel(new GridLayout(1,4));
        attackBtn = new JButton("Fight");
        skillBtn = new JButton("Skill");
        itemBtn = new JButton("Bag");
        fleeBtn = new JButton("Run");
        buttonPanel.add(attackBtn);
        buttonPanel.add(skillBtn);
        buttonPanel.add(itemBtn);
        buttonPanel.add(fleeBtn);

        playerPanel.add(playerLabel, BorderLayout.NORTH);
        playerPanel.add(playerHpBar, BorderLayout.CENTER);
        playerPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(enemyPanel, BorderLayout.NORTH);
        add(messagePanel, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    // Ask player to choose an item from inventory
public int askItemChoice(String message, String[] options) {
    int choice = JOptionPane.showOptionDialog(
        null,
        message,
        "Choose Item",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[0]
    );
    return choice; // returns the index selected
}

public int askEnemyChoice(String message, String[] options) {
    int choice = JOptionPane.showOptionDialog(
        null,
        message,
        "Choose Enemy",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.INFORMATION_MESSAGE,
        null,
        options,
        options[0]
    );
    return choice; // index of enemy selected
}
    // ===== Update HP bars =====
    public void setPlayerHp(int hp) { playerHpBar.setValue(hp); }
    public void setEnemyHp(int hp) { enemyHpBar.setValue(hp); }

    // ===== Update message =====
    public void setMessage(String msg) { messageLabel.setText(msg); }

    // ===== Add action listeners =====
    public void onAttack(ActionListener listener) { attackBtn.addActionListener(listener); }
    public void onSkill(ActionListener listener) { skillBtn.addActionListener(listener); }
    public void onItem(ActionListener listener) { itemBtn.addActionListener(listener); }
    public void onFlee(ActionListener listener) { fleeBtn.addActionListener(listener); }
}