package com.mycompany.rpg.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.InputStream;

public class MainMenuGUI extends JFrame {

    private JButton newGameBtn, loadGameBtn, exitBtn;

    // =========================================================
    // THEMED COLOR PALETTE CONFIGURATION (Synced with BattleGUI)
    // =========================================================
    private final Color COLOR_BG_MAIN      = new Color(30, 32, 44);   // Deep Navy Slate
    private final Color COLOR_BG_PANEL     = new Color(42, 45, 62);   // Lighter Slate
    private final Color COLOR_BG_CARD      = new Color(52, 56, 77);   // Button Baseline Card
    private final Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248); // Crisp White/Blue Text
    private final Color COLOR_TEXT_MUTED   = new Color(174, 190, 209); // Silvery Gray Text
    private final Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105); // Fantasy Gold
    private final Color COLOR_HP_RED       = new Color(235, 94, 85);   // Crimson Danger Accent
    private final Color COLOR_MP_BLUE      = new Color(83, 144, 217);  // Mystic Mana Blue
    private final Color COLOR_BORDER       = new Color(80, 86, 118);  // Inner border lines

    public MainMenuGUI() {
        setTitle("RPG Legends - Main Portal");
        setSize(480, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Core Layout Panel Wrapper Canvas
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(COLOR_BG_MAIN);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // =========================================================
        // NORTH: CINEMATIC RUNIC TITLE HEADER
        // =========================================================
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel decorationTop = new JLabel("⚡ ▬▬▬▬▬▬▬▬ ♦ ▬▬▬▬▬▬▬▬ ⚡", SwingConstants.CENTER);
        decorationTop.setFont(new Font("Monospaced", Font.BOLD, 12));
        decorationTop.setForeground(COLOR_BORDER);
        decorationTop.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("⚔ RPG LEGENDS ⚔", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        titleLabel.setForeground(COLOR_ACCENT_GOLD);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(10, 0, 2, 0));

        JLabel gameTaglineLabel = new JLabel("TRI-PANEL COMBAT ENGINE", SwingConstants.CENTER);
        gameTaglineLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        gameTaglineLabel.setForeground(COLOR_TEXT_MUTED);
        gameTaglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameTaglineLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        headerPanel.add(decorationTop);
        headerPanel.add(titleLabel);
        headerPanel.add(gameTaglineLabel);
        panel.add(headerPanel, BorderLayout.NORTH);

        // =========================================================
        // CENTER: INTERACTIVE NAVIGATIONAL CONTROL BUTTON STACK
        // =========================================================
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);

        Font btnFont = new Font("Monospaced", Font.BOLD, 14);
        
        // Instantiate buttons with custom text, assets, and unique color highlights
        newGameBtn  = createMenuButton("START NEW ADVENTURE", btnFont, "newGame.png", new Color(78, 186, 116));
        loadGameBtn = createMenuButton("LOAD PREVIOUS SAVE", btnFont, "loadGame.png", COLOR_MP_BLUE);
        exitBtn     = createMenuButton("EXIT GAME", btnFont, "exit.png", COLOR_HP_RED);

        buttonContainer.add(Box.createVerticalGlue());
        buttonContainer.add(newGameBtn);
        buttonContainer.add(Box.createVerticalStrut(16)); // Perfectly proportioned row separation
        buttonContainer.add(loadGameBtn);
        buttonContainer.add(Box.createVerticalStrut(16));
        buttonContainer.add(exitBtn);
        buttonContainer.add(Box.createVerticalGlue());

        panel.add(buttonContainer, BorderLayout.CENTER);

        // =========================================================
        // SOUTH: RENDERED SYSTEM SCORE FOOTER
        // =========================================================
        JPanel footerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        footerPanel.setOpaque(false);

        JLabel decorationBottom = new JLabel("⚡ ▬▬▬▬▬▬▬▬ ♦ ▬▬▬▬▬▬▬▬ ⚡", SwingConstants.CENTER);
        decorationBottom.setFont(new Font("Monospaced", Font.BOLD, 12));
        decorationBottom.setForeground(COLOR_BORDER);

        JLabel footerLabel = new JLabel("v1.0.0 — Stable Production Native Swing Build", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        footerLabel.setForeground(COLOR_TEXT_MUTED);

        footerPanel.add(decorationBottom);
        footerPanel.add(footerLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    // ===== THEMED DYNAMIC MOUSE ACTION BUTTON LAYOUT MOLD ENGINE =====
    private JButton createMenuButton(String text, Font font, String assetName, Color signatureColor) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(COLOR_BG_CARD);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        
        // Premium double inner line borders layout trim
        btn.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 20, 12, 20)
        ));
        
        // Solid constraint sizes
        btn.setMaximumSize(new Dimension(360, 52));
        btn.setPreferredSize(new Dimension(360, 52));

        // Inject asset resource image texture graphics mapping overlays safely
        ImageIcon icon = loadMenuStreamIcon(assetName, 20, 20);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(15);
        }

        // Connect smooth custom color highlighting states transitions loops
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(signatureColor);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(signatureColor.brighter(), 1, true),
                        new EmptyBorder(12, 20, 12, 20)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_BG_CARD);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(COLOR_BORDER, 1, true),
                        new EmptyBorder(12, 20, 12, 20)
                ));
            }
        });
        return btn;
    }

    public void onNewGame(ActionListener l) { newGameBtn.addActionListener(l); }
    public void onLoadGame(ActionListener l) { loadGameBtn.addActionListener(l); }
    public void onExitGame(ActionListener l) { exitBtn.addActionListener(l); }

    private ImageIcon loadMenuStreamIcon(String filename, int w, int h) {
        try {
            String primaryPath = "/com/mycompany/rpg/assets/" + filename;
            InputStream is = MainMenuGUI.class.getResourceAsStream(primaryPath);
            if (is == null) {
                is = MainMenuGUI.class.getResourceAsStream("/assets/" + filename);
            }
            if (is == null) return null;
            Image raw = ImageIO.read(is);
            is.close();
            if (raw == null) return null;
            return new ImageIcon(raw.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            return null;
        }
    }
}