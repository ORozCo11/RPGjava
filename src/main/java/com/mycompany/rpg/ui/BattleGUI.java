package com.mycompany.rpg.ui;

import com.mycompany.rpg.characters.Character;
import com.mycompany.rpg.enemies.Enemy;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class BattleGUI extends JFrame {

    private Character[] party;
    private Enemy[] enemies;

    private JPanel enemyPanel;
    private JPanel partyPanel;
    private JTextPane messagePane;
    private JButton attackBtn, skillBtn, itemBtn, fleeBtn;
    
    // Live wealth tracker component labels
    private JLabel coinCountLabel;

    private final java.util.List<JProgressBar> enemyHpBars = new ArrayList<>();
    private final java.util.List<JLabel> enemyLabels = new ArrayList<>();

    private final java.util.List<JProgressBar> partyHpBars = new ArrayList<>();
    private final java.util.List<JProgressBar> partyMpBars = new ArrayList<>();
    private final java.util.List<JLabel> partyLabels = new ArrayList<>();

    private final LinkedList<String> messageLines = new LinkedList<>();
    private Runnable closeCallback;
    private JMenuItem returnToMenuItem; // Tracks the return-to-menu item handle

    // ===== TURN TRACKING VARIABLES =====
    private int activePartyIndex = 0;    // Tracks which player is active (0-3)
    private int activeEnemyIndex = -1;   // Tracks active enemy loop cursor (-1 means Player Phase)

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
        setSize(1050, 620);
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                triggerAutoSaveAndExit();
            }
        });

        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(COLOR_BG_MAIN);

        Font titleFont = new Font("Monospaced", Font.BOLD, 20);
        Font labelFont = new Font("Monospaced", Font.BOLD, 14);

        // =========================================================
        // NORTH: HEADER BANNER WITH UPPER-RIGHT COIN INDICATOR
        // =========================================================
        JPanel headerWrapperPanel = new JPanel(new BorderLayout(10, 0));
        headerWrapperPanel.setBackground(COLOR_BG_PANEL);
        headerWrapperPanel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 2, true),
                new EmptyBorder(8, 15, 8, 15)
        ));

        JLabel titleLabel = new JLabel("⚔ RPG TRI-PANEL COMBAT ENGINE ⚔", SwingConstants.LEFT);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(COLOR_ACCENT_GOLD);
        headerWrapperPanel.add(titleLabel, BorderLayout.WEST);

        // Coin Tracker Module docking layout container
        JPanel coinDisplayPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        coinDisplayPanel.setOpaque(false);

        // Load the coin image sprite
        ImageIcon coinRawIcon = createScaledIcon("/com/mycompany/rpg/assets/coin.png", 22, 22);
        if (coinRawIcon == null) {
            coinRawIcon = createScaledIcon("/assets/coin.png", 22, 22);
        }

        JLabel coinIconLabel = new JLabel();
        if (coinRawIcon != null) {
            coinIconLabel.setIcon(coinRawIcon);
        } else {
            coinIconLabel.setText("🪙");
            coinIconLabel.setFont(new Font("Serif", Font.PLAIN, 18));
        }

        coinCountLabel = new JLabel("0");
        coinCountLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        coinCountLabel.setForeground(COLOR_ACCENT_GOLD);

        coinDisplayPanel.add(coinIconLabel);
        coinDisplayPanel.add(coinCountLabel);
        headerWrapperPanel.add(coinDisplayPanel, BorderLayout.EAST);
        
        add(headerWrapperPanel, BorderLayout.NORTH);

        // =========================================================
        // CENTER: CORE HORIZONTAL SIDE-BY-SIDE INTERFACE GRID
        // =========================================================
        JPanel screenSplitGrid = new JPanel(new GridLayout(1, 3, 12, 0));
        screenSplitGrid.setBackground(COLOR_BG_MAIN);
        screenSplitGrid.setBorder(new EmptyBorder(0, 12, 0, 12));

        partyPanel = new JPanel();
        partyPanel.setBackground(COLOR_BG_PANEL);
        TitledBorder partyTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " YOUR PARTY ", TitledBorder.CENTER, TitledBorder.TOP, labelFont, COLOR_MP);
        partyPanel.setBorder(new CompoundBorder(partyTitle, new EmptyBorder(10, 10, 10, 10)));

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(COLOR_BG_PANEL);
        TitledBorder logTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " COMBAT LOG ", TitledBorder.CENTER, TitledBorder.TOP, labelFont, COLOR_ACCENT_GOLD);
        messagePanel.setBorder(new CompoundBorder(logTitle, new EmptyBorder(10, 10, 10, 10)));

        messagePane = new JTextPane();
        messagePane.setEditable(false);
        messagePane.setContentType("text/html"); 
        messagePane.setBackground(COLOR_BAR_BG);
        messagePane.setForeground(COLOR_TEXT_LIGHT);

        JScrollPane messageScroll = new JScrollPane(messagePane);
        messageScroll.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        messagePanel.add(messageScroll, BorderLayout.CENTER);

        enemyPanel = new JPanel();
        enemyPanel.setBackground(COLOR_BG_PANEL);
        TitledBorder enemyTitle = new TitledBorder(new LineBorder(COLOR_BORDER, 1, true), " TARGET ENEMIES ", TitledBorder.CENTER, TitledBorder.TOP, labelFont, COLOR_HP);
        enemyPanel.setBorder(new CompoundBorder(enemyTitle, new EmptyBorder(10, 10, 10, 10)));

        screenSplitGrid.add(partyPanel);
        screenSplitGrid.add(messagePanel);
        screenSplitGrid.add(enemyPanel);
        add(screenSplitGrid, BorderLayout.CENTER);

        // =========================================================
        // SOUTH: ACTION BUTTON PANEL
        // =========================================================
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setBackground(COLOR_BG_MAIN);
        buttonPanel.setBorder(new EmptyBorder(5, 15, 15, 15));

        attackBtn = createActionIconButton("FIGHT (A)", "fight.png", labelFont);
        skillBtn  = createActionIconButton("SKILL (S)", "skill.png", labelFont);
        itemBtn   = createActionIconButton("BAG (I)", "bag.png", labelFont);
        fleeBtn   = createActionIconButton("RUN (R)", "run.png", labelFont);

        buttonPanel.add(attackBtn);
        buttonPanel.add(skillBtn);
        buttonPanel.add(itemBtn);
        buttonPanel.add(fleeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_BG_PANEL);
        menuBar.setBorder(new LineBorder(COLOR_BORDER, 1));
        JMenu gameMenu = new JMenu(" GAME OPTIONS ");
        gameMenu.setFont(new Font("Monospaced", Font.BOLD, 13));
        gameMenu.setForeground(COLOR_ACCENT_GOLD);
        
        // 1. Desktop Exit Option
        JMenuItem saveExitItem = new JMenuItem("Save & Exit to Desktop");
        saveExitItem.setFont(new Font("Monospaced", Font.PLAIN, 12));
        saveExitItem.setBackground(COLOR_BG_CARD);
        saveExitItem.setForeground(COLOR_TEXT_LIGHT);
        saveExitItem.addActionListener(e -> triggerAutoSaveAndExit());
        gameMenu.add(saveExitItem);

        // 2. Main Menu Return Option
        returnToMenuItem = new JMenuItem("Save & Return to Main Menu");
        returnToMenuItem.setFont(new Font("Monospaced", Font.PLAIN, 12));
        returnToMenuItem.setBackground(COLOR_BG_CARD);
        returnToMenuItem.setForeground(COLOR_TEXT_LIGHT);
        gameMenu.add(returnToMenuItem);
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        refreshEnemies();
        refreshParty();
        setVisible(true);
    }

    // ===== FIXED: RE-INJECTED ACCIDENTALLY DELETED BUTTON BUILDER HELPER METHOD =====
    private JButton createActionIconButton(String labelText, String fileName, Font textFont) {
        JButton btn = new JButton(labelText);
        btn.setFont(textFont);
        btn.setBackground(COLOR_BTN_DEFAULT);
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(new LineBorder(COLOR_BORDER, 2, true));
        btn.setPreferredSize(new Dimension(0, 45));

        ImageIcon btnIcon = createScaledIcon("/com/mycompany/rpg/assets/" + fileName, 22, 22);
        if (btnIcon == null) {
            btnIcon = createScaledIcon("/assets/" + fileName, 22, 22);
        }

        if (btnIcon != null) {
            btn.setIcon(btnIcon);
            btn.setHorizontalTextPosition(SwingConstants.RIGHT);
            btn.setIconTextGap(10);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(COLOR_BTN_DEFAULT.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(COLOR_BTN_DEFAULT); }
        });
        return btn;
    }

    public void updateCoinCount(int currentCoins) {
        if (coinCountLabel != null) {
            coinCountLabel.setText(String.valueOf(currentCoins));
        }
    }

    private void customizeProgressBar(JProgressBar bar, Color fillStyle) {
        bar.setOpaque(true);
        bar.setBackground(COLOR_BAR_BG);
        bar.setForeground(fillStyle);
        bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override protected Color getSelectionBackground() { return COLOR_TEXT_LIGHT; }
            @Override protected Color getSelectionForeground() { return COLOR_TEXT_LIGHT; }
        });
        bar.setBorder(new LineBorder(COLOR_BG_MAIN, 1));
    }

    // ===== TURN HIGH-LIGHT CONTROLLER SYSTEM =====
    public void setActiveTurn(int partyIndex, int enemyIndex) {
        this.activePartyIndex = partyIndex;
        this.activeEnemyIndex = enemyIndex;
        refreshParty();
        refreshEnemies();
    }

    public void setEnemies(Enemy[] enemies) { this.enemies = enemies; refreshEnemies(); }
    public void setParty(Character[] party) { this.party = party; refreshParty(); }

    public void refreshEnemies() {
        enemyPanel.removeAll();
        enemyHpBars.clear();
        enemyLabels.clear();

        int enemyCount = (enemies == null || enemies.length == 0) ? 1 : enemies.length;
        enemyPanel.setLayout(new GridLayout(enemyCount, 1, 0, 8));

        if (enemies != null) {
            for (int i = 0; i < enemies.length; i++) {
                Enemy e = enemies[i];
                boolean isThisEnemyActive = (i == activeEnemyIndex);

                JPanel card = new JPanel(new BorderLayout(8, 4));
                
                if (isThisEnemyActive) {
                    card.setBackground(new Color(77, 45, 50)); 
                    card.setBorder(new CompoundBorder(
                            new LineBorder(COLOR_HP, 2, true),
                            new EmptyBorder(7, 7, 7, 7)
                    ));
                } else {
                    card.setBackground(COLOR_BG_CARD);
                    card.setBorder(new CompoundBorder(
                            new LineBorder(COLOR_BORDER, 1, true),
                            new EmptyBorder(8, 8, 8, 8)
                    ));
                }

                String rawName = e.getName().toLowerCase();
                String spritePath = "/com/mycompany/rpg/assets/slime.png"; 
                if (rawName.contains("boss") || rawName.contains("warlord") || rawName.contains("troll")) {
                    spritePath = "/com/mycompany/rpg/assets/bossTroll.png";
                } else if (rawName.contains("goblin")) {
                    spritePath = "/com/mycompany/rpg/assets/goblin.png";
                } else if (rawName.contains("skeleton") || rawName.contains("bone") || rawName.contains("lich")) {
                    spritePath = "/com/mycompany/rpg/assets/skeleton.png";
                } else if (rawName.contains("vampire") || rawName.contains("bat")) {
                    spritePath = "/com/mycompany/rpg/assets/vampire.png";
                }

                ImageIcon enemyIcon = createScaledIcon(spritePath, 45, 45);
                JLabel imageLabel = (enemyIcon != null) ? new JLabel(enemyIcon, SwingConstants.CENTER) : new JLabel("", SwingConstants.CENTER);

                String namePrefix = isThisEnemyActive ? "👹 ▶ " : "";
                JLabel nameLabel = new JLabel(namePrefix + e.getName(), SwingConstants.CENTER);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, isThisEnemyActive ? 14 : 13));
                nameLabel.setForeground(isThisEnemyActive ? COLOR_HP : COLOR_TEXT_LIGHT);

                JPanel identityPanel = new JPanel(new BorderLayout(8, 0));
                identityPanel.setOpaque(false);
                identityPanel.add(imageLabel, BorderLayout.WEST);
                identityPanel.add(nameLabel, BorderLayout.CENTER);

                JProgressBar hpBar = new JProgressBar(0, e.getMaxHp());
                hpBar.setValue(e.getHp());
                hpBar.setString(e.getHp() + " / " + e.getMaxHp() + " HP");
                hpBar.setStringPainted(true);
                hpBar.setFont(new Font("Monospaced", Font.BOLD, 12));
                customizeProgressBar(hpBar, COLOR_HP);

                card.add(identityPanel, BorderLayout.NORTH);
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
        partyPanel.setLayout(new GridLayout(partyCount, 1, 0, 8));

        if (party != null) {
            for (int i = 0; i < party.length; i++) {
                Character c = party[i];
                boolean isThisHeroActive = (i == activePartyIndex && activeEnemyIndex == -1);

                JPanel card = new JPanel(new BorderLayout(8, 2));
                
                if (isThisHeroActive) {
                    card.setBackground(new Color(62, 66, 91)); 
                    card.setBorder(new CompoundBorder(
                            new LineBorder(COLOR_ACCENT_GOLD, 2, true),
                            new EmptyBorder(7, 7, 7, 7)
                    ));
                } else {
                    card.setBackground(COLOR_BG_CARD);
                    card.setBorder(new CompoundBorder(
                            new LineBorder(COLOR_BORDER, 1, true),
                            new EmptyBorder(8, 8, 8, 8)
                    ));
                }

                String classTitle = c.getClass().getSimpleName(); 
                String lowerName = classTitle.toLowerCase();     
                
                String mainPath = "/com/mycompany/rpg/assets/" + lowerName + ".png";
                String fallbackPath = "/assets/" + lowerName + ".png";
                
                if (c.getName() != null && c.getName().equalsIgnoreCase("Freya")) {
                    mainPath = "/com/mycompany/rpg/assets/womanwarrior.png";
                    fallbackPath = "/assets/womanwarrior.png";
                }
                
                ImageIcon classIcon = createScaledIcon(mainPath, 45, 45);
                if (classIcon == null) {
                    classIcon = createScaledIcon(fallbackPath, 45, 45);
                }

                JLabel imageLabel;
                if (classIcon != null) {
                    imageLabel = new JLabel(classIcon, SwingConstants.CENTER);
                } else {
                    imageLabel = new JLabel("[ " + classTitle.substring(0, 1) + " ]", SwingConstants.CENTER);
                    imageLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
                    imageLabel.setForeground(COLOR_ACCENT_GOLD);
                }

                String turnIndicatorBadge = isThisHeroActive ? "⚔ ▶ " : "";
                JLabel nameLabel = new JLabel(turnIndicatorBadge + c.getName() + " (" + classTitle + ")", SwingConstants.LEFT);
                nameLabel.setFont(new Font("Monospaced", Font.BOLD, isThisHeroActive ? 14 : 13));
                nameLabel.setForeground(COLOR_ACCENT_GOLD);

                JPanel identityPanel = new JPanel(new BorderLayout(8, 0));
                identityPanel.setOpaque(false);
                identityPanel.add(imageLabel, BorderLayout.WEST);
                identityPanel.add(nameLabel, BorderLayout.CENTER);

                JPanel barsPanel = new JPanel(new GridLayout(2, 1, 0, 4));
                barsPanel.setBackground(COLOR_BG_CARD);
                barsPanel.setOpaque(false);
                
                JProgressBar hpBar = new JProgressBar(0, c.getMaxHp());
                hpBar.setValue(c.getHp());
                hpBar.setString("HP: " + c.getHp() + " / " + c.getMaxHp());
                hpBar.setStringPainted(true);
                hpBar.setFont(new Font("Monospaced", Font.BOLD, 11));
                customizeProgressBar(hpBar, COLOR_HP);

                JProgressBar mpBar = new JProgressBar(0, c.getMaxMp());
                mpBar.setValue(c.getMp());
                mpBar.setString("MP: " + c.getMp() + " / " + c.getMaxMp());
                mpBar.setStringPainted(true);
                mpBar.setFont(new Font("Monospaced", Font.BOLD, 11));
                customizeProgressBar(mpBar, COLOR_MP);

                barsPanel.add(hpBar);
                barsPanel.add(mpBar);

                card.add(identityPanel, BorderLayout.NORTH);
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

    public void setPlayerHp(int hp) { refreshParty(); }
    public void setEnemyHp(int hp) { refreshEnemies(); }
    public void updatePartyBars() { refreshParty(); }
    public void updateEnemyBars() { refreshEnemies(); }

    private String getHtmlIconTag(String filename) {
        try {
            String primaryPath = "/com/mycompany/rpg/assets/" + filename;
            java.net.URL url = BattleGUI.class.getResource(primaryPath);
            if (url == null) {
                url = BattleGUI.class.getResource("/assets/" + filename);
            }
            if (url != null) {
                return "<img src='" + url.toExternalForm() + "' width='16' height='16' style='vertical-align: middle; margin: 0 4px;'>";
            }
        } catch (Exception e) {
            // quiet fallback
        }
        return ""; 
    }

    public void setMessage(String msg) {
        if (messageLines.size() >= 7) messageLines.removeFirst(); 

        String styledRow = msg;
        String lower = msg.toLowerCase();
        String iconPrefix = "";
        String iconSuffix = "";

        if (lower.contains("attacks") || lower.contains("swung") || lower.contains("hit") || lower.contains("slams")) {
            iconPrefix = getHtmlIconTag("fight.png");
        } else if (lower.contains("heavy strike")) {
            iconPrefix = getHtmlIconTag("heavyStrike.png");
        } else if (lower.contains("shield slam")) {
            iconPrefix = getHtmlIconTag("shieldSlam.png");
        } else if (lower.contains("fireball")) {
            iconPrefix = getHtmlIconTag("fireball.png");
        } else if (lower.contains("lightning") || lower.contains("bolt") || lower.contains("squall")) {
            iconPrefix = getHtmlIconTag("lightningBolt.png");
        } else if (lower.contains("piercing shot")) {
            iconPrefix = getHtmlIconTag("piercingShot.png");
        } else if (lower.contains("arrow rain")) {
            iconPrefix = getHtmlIconTag("arrowRain.png");
        } else if (lower.contains("health potion")) {
            iconPrefix = getHtmlIconTag("healthPotion.png");
        } else if (lower.contains("mana elixir") || lower.contains("drinks")) {
            iconPrefix = getHtmlIconTag("mpPotion.png");
        } else if (lower.contains("revive scroll")) {
            iconPrefix = getHtmlIconTag("reviveScroll.png");
        } else if (lower.contains("earned") || lower.contains("coins") || lower.contains("gold")) {
            iconPrefix = getHtmlIconTag("coin.png");
            iconSuffix = getHtmlIconTag("coin.png");
        }

        if (lower.contains("crit") || lower.contains("critical") || lower.contains("resurrected") || lower.contains("✨")) {
            styledRow = iconPrefix + "<span style='color: #FFD269; font-weight: bold;'>" + msg + "</span>" + iconSuffix;
        } else if (lower.contains("cleared") || lower.contains("earned") || lower.contains("victory") || lower.contains("recovered") || lower.contains("recovers") || lower.contains("boosted")) {
            styledRow = iconPrefix + "<span style='color: #4EBA74; font-weight: bold;'>" + msg + "</span>" + iconSuffix;
        } else if (lower.contains("casts") || lower.contains("fireball") || lower.contains("lightning") || lower.contains("shot") || lower.contains("strike") || lower.contains("slam")) {
            styledRow = iconPrefix + "<span style='color: #5390D9; font-weight: bold;'>" + msg + "</span>" + iconSuffix;
        } else if (lower.contains("attacks") || lower.contains("damage") || lower.contains("takes") || lower.contains("slams") || lower.contains("cleave") || lower.contains("missed") || lower.contains("lost")) {
            styledRow = iconPrefix + "<span style='color: #EB5E55;'>" + msg + "</span>" + iconSuffix;
        } else if (lower.contains("begins") || lower.contains("turn!")) {
            styledRow = iconPrefix + "<span style='color: #AEBED1; font-style: italic;'>" + msg + "</span>" + iconSuffix;
        } else {
            styledRow = iconPrefix + "<span style='color: #F0F4F8;'> " + msg + "</span>" + iconSuffix;
        }

        messageLines.add(styledRow);

        StringBuilder htmlFeedBuilder = new StringBuilder();
        htmlFeedBuilder.append("<html><body style='font-family:Monospaced; font-size:12px; text-align:center; margin:0; padding:0;'>");
        for (String line : messageLines) {
            htmlFeedBuilder.append("<p style='margin:6px 0; padding:0;'>").append(line).append("</p>");
        }
        htmlFeedBuilder.append("</body></html>");

        messagePane.setText(htmlFeedBuilder.toString());
    }

    public void onAttack(ActionListener listener) { attackBtn.addActionListener(listener); }
    public void onSkill(ActionListener listener) { skillBtn.addActionListener(listener); }
    public void onItem(ActionListener listener) { itemBtn.addActionListener(listener); }
    public void onFlee(ActionListener listener) { fleeBtn.addActionListener(listener); }
    public void onReturnToMainMenu(ActionListener listener) { returnToMenuItem.addActionListener(listener); }
    public void onWindowCloseRequest(Runnable callback) { this.closeCallback = callback; }

    // ===== THEMED BASE DIALOG CARD COMPONENT ENGINE =====
    private int showStyledOptionDialog(String titleHeader, String messageBody, String[] options, String iconFile) {
        final int[] selection = {-1};

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 12));
        dialogPanel.setBackground(COLOR_BG_PANEL);
        dialogPanel.setPreferredSize(new Dimension(460, 240));
        dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("⚔ " + titleHeader + " ⚔", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_ACCENT_GOLD);
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new BorderLayout(15, 0));
        centerCard.setBackground(COLOR_BG_CARD);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        if (iconFile != null) {
            ImageIcon icon = createScaledIcon("/com/mycompany/rpg/assets/" + iconFile, 24, 24);
            if (icon == null) icon = createScaledIcon("/assets/" + iconFile, 24, 24);
            if (icon != null) centerCard.add(new JLabel(icon), BorderLayout.WEST);
        }

        JLabel textLabel = new JLabel("<html><body style='width: 310px; font-family:Monospaced; font-size:12px; color:#F0F4F8;'>" 
                + messageBody.replaceAll("\n", "<br>") + "</body></html>");
        centerCard.add(textLabel, BorderLayout.CENTER);
        dialogPanel.add(centerCard, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, options.length, 12, 0));
        buttonsPanel.setOpaque(false);

        for (int i = 0; i < options.length; i++) {
            JButton btn = new JButton(options[i]);
            btn.setFont(new Font("Monospaced", Font.BOLD, 12));
            btn.setForeground(COLOR_TEXT_LIGHT);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(0, 38));

            String optLower = options[i].toLowerCase();
            final Color uniqueBtnBg;
            
            if (optLower.contains("escape") || optLower.contains("flee") || optLower.contains("run") || optLower.contains("exit") || optLower.contains("deny") || optLower.contains("return")) {
                uniqueBtnBg = COLOR_HP; // Crimson Red
            } else if (optLower.contains("stay") || optLower.contains("fight") || optLower.contains("start") || optLower.contains("accept") || optLower.contains("ok") || optLower.contains("revive") || optLower.contains("continue")) {
                uniqueBtnBg = new Color(78, 186, 116); // Forest Emerald Green
            } else {
                uniqueBtnBg = COLOR_BTN_DEFAULT; 
            }

            btn.setBackground(uniqueBtnBg);
            btn.setBorder(new LineBorder(uniqueBtnBg.brighter(), 1, true));

            final Color hoverColor = uniqueBtnBg.brighter();
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(hoverColor); }
                @Override public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(uniqueBtnBg); }
            });
            
            final int index = i;
            btn.addActionListener(clickEvent -> {
                selection[0] = index;
                Window win = SwingUtilities.getWindowAncestor(btn);
                if (win != null) win.dispose(); 
            });
            
            buttonsPanel.add(btn);
        }
        dialogPanel.add(buttonsPanel, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);

        JOptionPane.showOptionDialog(
                this, dialogPanel, titleHeader,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );

        return selection[0];
    }

    public int askEnemyChoice(String message, String[] options) {
        return showStyledOptionDialog("CHOOSE TARGET", message, options, "fight.png");
    }

    public int askItemChoice(String message, String[] options) {
        return showStyledOptionDialog("OPEN BAG - SELECT ITEM", message, options, "bag.png");
    }

    public int askFleeChoice(String message, String[] options) {
        return showStyledOptionDialog("ATTEMPT ESCAPE?", message, options, "run.png");
    }

    public int askReviveChoice(String message, String[] options) {
        return showStyledOptionDialog("USE REVIVE SCROLL", message, options, "reviveScroll.png");
    }

    // ===== ENEMY PHASE CINEMATIC OVERLAY =====
    public void showEnemyAction(String message, String enemyName) {
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 12));
        dialogPanel.setBackground(COLOR_BG_PANEL);
        dialogPanel.setPreferredSize(new Dimension(460, 270)); 
        dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("⚔ ENEMY PHASE ⚔", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_HP);
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new BorderLayout(0, 12));
        centerCard.setBackground(COLOR_BG_CARD);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        String rawName = enemyName.toLowerCase();
        String spritePath = "/com/mycompany/rpg/assets/slime.png"; 
        if (rawName.contains("boss") || rawName.contains("warlord") || rawName.contains("troll")) {
            spritePath = "/com/mycompany/rpg/assets/bossTroll.png";
        } else if (rawName.contains("goblin")) {
            spritePath = "/com/mycompany/rpg/assets/goblin.png";
        } else if (rawName.contains("skeleton") || rawName.contains("bone") || rawName.contains("lich")) {
            spritePath = "/com/mycompany/rpg/assets/skeleton.png";
        } else if (rawName.contains("vampire") || rawName.contains("bat")) {
            spritePath = "/com/mycompany/rpg/assets/vampire.png";
        }

        ImageIcon enemyPortraitIcon = createScaledIcon(spritePath, 64, 64);
        if (enemyPortraitIcon != null) {
            JLabel imageLabel = new JLabel(enemyPortraitIcon, SwingConstants.CENTER);
            centerCard.add(imageLabel, BorderLayout.NORTH);
        }

        JLabel textLabel = new JLabel("<html><body style='width: 310px; font-family:Monospaced; font-size:13px; color:#F0F4F8; text-align:center;'> " 
                + message.replaceAll("\n", "<br>") + "</body></html>", SwingConstants.CENTER);
        centerCard.add(textLabel, BorderLayout.CENTER);
        dialogPanel.add(centerCard, BorderLayout.CENTER);

        JButton btn = new JButton("OK");
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setBackground(COLOR_HP); 
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setBorder(new LineBorder(COLOR_HP.brighter(), 1, true));

        btn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(btn);
            if (win != null) win.dispose();
        });
        dialogPanel.add(btn, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);

        JOptionPane.showOptionDialog(
                this, dialogPanel, "ENEMY PHASE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );
    }

    // ===== PLAYER CINEMATIC ACTION PORTRAIT POPUP MODAL =====
    public void showPlayerAction(String message, String characterName, String classTitle) {
        JPanel dialogPanel = new JPanel(new BorderLayout(0, 12));
        dialogPanel.setBackground(COLOR_BG_PANEL);
        dialogPanel.setPreferredSize(new Dimension(460, 270)); 
        dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("⚔ PLAYER PHASE ⚔", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_MP); 
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new BorderLayout(0, 12));
        centerCard.setBackground(COLOR_BG_CARD);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));

        String lowerName = classTitle.toLowerCase();
        String spritePath = "/com/mycompany/rpg/assets/" + lowerName + ".png";
        if (characterName != null && characterName.equalsIgnoreCase("Freya")) {
            spritePath = "/com/mycompany/rpg/assets/womanwarrior.png";
        }

        ImageIcon playerPortraitIcon = createScaledIcon(spritePath, 64, 64);
        if (playerPortraitIcon != null) {
            JLabel imageLabel = new JLabel(playerPortraitIcon, SwingConstants.CENTER);
            centerCard.add(imageLabel, BorderLayout.NORTH);
        }

        JLabel textLabel = new JLabel("<html><body style='width: 310px; font-family:Monospaced; font-size:13px; color:#F0F4F8; text-align:center;'> " 
                + message.replaceAll("\n", "<br>") + "</body></html>", SwingConstants.CENTER);
        centerCard.add(textLabel, BorderLayout.CENTER);
        dialogPanel.add(centerCard, BorderLayout.CENTER);

        JButton btn = new JButton("CONTINUE");
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setBackground(COLOR_MP); 
        btn.setForeground(COLOR_TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.setBorder(new LineBorder(COLOR_MP.brighter(), 1, true));

        btn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(btn);
            if (win != null) win.dispose();
        });
        dialogPanel.add(btn, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);

        JOptionPane.showOptionDialog(
                this, dialogPanel, "PLAYER PHASE",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );
    }

    public void showThemedNotification(String header, String message) {
        showStyledOptionDialog(header, message, new String[]{"OK"}, "bag.png");
    }

    public int showThemedConfirmDialog(String header, String message, String[] choices) {
        return showStyledOptionDialog(header, message, choices, "run.png");
    }

    // ===== FIXED: ENTIRELY THEMED AUTOSAVE NOTIFICATION WITH GLOWING DISMISS BUTTON =====
    private void triggerAutoSaveAndExit() {
        if (closeCallback != null) closeCallback.run();

        JPanel dialogPanel = new JPanel(new BorderLayout(0, 12));
        dialogPanel.setBackground(COLOR_BG_PANEL);
        dialogPanel.setPreferredSize(new Dimension(460, 240));
        dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("💾 PROGRESS AUTOSAVED 💾", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        titleLabel.setForeground(COLOR_ACCENT_GOLD);
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerCard = new JPanel(new BorderLayout(0, 12));
        centerCard.setBackground(COLOR_BG_CARD);
        centerCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel textLabel = new JLabel("<html><body style='width: 340px; font-family:Monospaced; font-size:13px; color:#F0F4F8; text-align:center;'>"
                + "Your session file data has been safely written to disk!<br><br><font color='#AEBED1'>Goodbye, Adventurer.</font></body></html>", SwingConstants.CENTER);
        centerCard.add(textLabel, BorderLayout.CENTER);
        dialogPanel.add(centerCard, BorderLayout.CENTER);

        JButton exitBtn = new JButton("ACKNOWLEDGE & EXIT");
        exitBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        exitBtn.setBackground(new Color(78, 186, 116)); 
        exitBtn.setForeground(COLOR_TEXT_LIGHT);
        exitBtn.setFocusPainted(false);
        exitBtn.setPreferredSize(new Dimension(0, 40));
        exitBtn.setBorder(new LineBorder(new Color(78, 186, 116).brighter(), 1, true));

        exitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) { exitBtn.setBackground(new Color(78, 186, 116).brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) { exitBtn.setBackground(new Color(78, 186, 116)); }
        });

        exitBtn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(exitBtn);
            if (win != null) win.dispose();
            dispose();
            System.exit(0);
        });
        dialogPanel.add(exitBtn, BorderLayout.SOUTH);

        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);

        JOptionPane.showOptionDialog(
                this, dialogPanel, "SYSTEM DATA SAVED",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );

        dispose();
        System.exit(0);
    }

    private static ImageIcon createScaledIcon(String path, int width, int height) {
        try {
            InputStream is = BattleGUI.class.getResourceAsStream(path);
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