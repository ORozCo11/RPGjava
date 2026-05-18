package com.mycompany.rpg.utils;

import com.mycompany.rpg.items.Item;
import com.mycompany.rpg.ui.BattleGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;

public class Shop {

    // Sharing your exact modern dark RPG color profile
    private static final Color COLOR_BG_PANEL     = new Color(42, 45, 62);   
    private static final Color COLOR_BG_CARD      = new Color(52, 56, 77);   
    private static final Color COLOR_TEXT_LIGHT   = new Color(240, 244, 248); 
    private static final Color COLOR_TEXT_MUTED   = new Color(174, 190, 209); 
    private static final Color COLOR_ACCENT_GOLD  = new Color(255, 210, 105); 
    private static final Color COLOR_BORDER       = new Color(80, 86, 118);  
    private static final Color COLOR_HP_RED       = new Color(235, 94, 85);  // Crimson for warnings/exits

    public static int openShop(BattleGUI parent, ArrayList<Item> inventory, int currentCoins) {
        // Wrapper container to pass multiple values into the tracker frame anonymously
        int[] playerWallet = { currentCoins };

        // 1. Build a specialized vertical column shop panel
        JPanel shopWrapper = new JPanel(new BorderLayout(0, 15));
        shopWrapper.setBackground(COLOR_BG_PANEL);
        shopWrapper.setPreferredSize(new Dimension(480, 340)); // Adjusted for bottom control padding

        // Live Header status bar showing remaining gold balance side-by-side with your coin image!
        JPanel balanceRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        balanceRow.setOpaque(false);

        JLabel walletTextLabel = new JLabel("WELCOME TO THE SHOP! WALLET:");
        walletTextLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        walletTextLabel.setForeground(COLOR_TEXT_LIGHT);

        ImageIcon coinIcon = loadScaledShopIcon("coin.png", 20, 20);
        JLabel coinImgLabel = (coinIcon != null) ? new JLabel(coinIcon) : new JLabel("🪙");

        JLabel liveBalanceLabel = new JLabel(playerWallet[0] + " COINS");
        liveBalanceLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        liveBalanceLabel.setForeground(COLOR_ACCENT_GOLD);

        balanceRow.add(walletTextLabel);
        balanceRow.add(coinImgLabel);
        balanceRow.add(liveBalanceLabel);
        shopWrapper.add(balanceRow, BorderLayout.NORTH);

        // 2. Setup the catalog grid list layout panel
        JPanel catalogPanel = new JPanel();
        catalogPanel.setLayout(new BoxLayout(catalogPanel, BoxLayout.Y_AXIS));
        catalogPanel.setBackground(COLOR_BG_PANEL);

        // Define stock catalogs metrics variables arrays map registers
        String[] itemNames = {"Health Potion", "Mana Elixir", "Revive Scroll"};
        String[] itemDescs = {"Restores 30 HP", "Restores 20 MP", "Revives a fallen ally"};
        int[] itemPrices = {10, 10, 10};
        int[] itemEffects = {30, 20, 50};
        String[] itemFiles = {"healthPotion.png", "mpPotion.png", "reviveScroll.png"};

        for (int i = 0; i < itemNames.length; i++) {
            final int index = i;
            
            JPanel itemRow = new JPanel(new BorderLayout(10, 0));
            itemRow.setBackground(COLOR_BG_CARD);
            itemRow.setMaximumSize(new Dimension(460, 50));
            itemRow.setPreferredSize(new Dimension(460, 50));
            itemRow.setBorder(new CompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(5, 10, 5, 10)
            ));

            // Load specific item image portrait texture mapping
            ImageIcon rowIcon = loadScaledShopIcon(itemFiles[i], 24, 24);
            JLabel rowImgLabel = (rowIcon != null) ? new JLabel(rowIcon) : new JLabel("📦");

            JLabel nameDescLabel = new JLabel("<html><b>" + itemNames[i] + "</b><br><font color='#AEBED1'>" + itemDescs[i] + "</font></html>");
            nameDescLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            nameDescLabel.setForeground(COLOR_TEXT_LIGHT);

            JPanel infoSubGapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            infoSubGapPanel.setOpaque(false);
            infoSubGapPanel.add(rowImgLabel);
            infoSubGapPanel.add(nameDescLabel);
            itemRow.add(infoSubGapPanel, BorderLayout.WEST);

            // Purchase action trigger mechanism button layout
            JButton buyBtn = new JButton(itemPrices[i] + " Gold");
            buyBtn.setFont(new Font("Monospaced", Font.BOLD, 12));
            buyBtn.setBackground(COLOR_BORDER);
            buyBtn.setForeground(COLOR_TEXT_LIGHT);
            buyBtn.setFocusPainted(false);
            buyBtn.setPreferredSize(new Dimension(95, 30));

            buyBtn.addActionListener(click -> {
                if (playerWallet[0] < itemPrices[index]) {
                    // ===== UPGRADED: NATIVE BOX CONVERTED TO PREMIUM DENIAL CARD PANEL =====
                    showThemedInsufficientFundsDialog(shopWrapper, playerWallet[0]);
                    return;
                }
                
                // Deduct cost and add bought item to shared inventory array context tracking lists
                playerWallet[0] -= itemPrices[index];
                inventory.add(new Item(itemNames[index], itemDescs[index], itemEffects[index]));
                
                // Live re-sync dialog text frame parameters states
                liveBalanceLabel.setText(playerWallet[0] + " COINS");
                parent.setMessage("Bought " + itemNames[index] + " for " + itemPrices[index] + " coins.");
                parent.updateCoinCount(playerWallet[0]); // Dynamically update main screen coins instantly!
            });

            itemRow.add(buyBtn, BorderLayout.EAST);
            catalogPanel.add(itemRow);
            catalogPanel.add(Box.createVerticalStrut(6));
        }

        JScrollPane catalogScroll = new JScrollPane(catalogPanel);
        catalogScroll.setBorder(null);
        catalogScroll.setOpaque(false);
        catalogScroll.getViewport().setOpaque(false);
        shopWrapper.add(catalogScroll, BorderLayout.CENTER);

        // ===== UPGRADED: PREMIUM CUSTOM RED ACTION BUTTON INSERTED AT SOUTH PANEL =====
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(10, 0, 5, 0));

        JButton closeShopBtn = new JButton("⚔ CLOSE SHOP & CONTINUE JOURNEY ⚔");
        closeShopBtn.setFont(new Font("Monospaced", Font.BOLD, 14));
        closeShopBtn.setBackground(COLOR_HP_RED);
        closeShopBtn.setForeground(COLOR_TEXT_LIGHT);
        closeShopBtn.setFocusPainted(false);
        closeShopBtn.setPreferredSize(new Dimension(0, 42));
        closeShopBtn.setBorder(new LineBorder(COLOR_HP_RED.brighter(), 1, true));

        closeShopBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) { closeShopBtn.setBackground(COLOR_HP_RED.brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) { closeShopBtn.setBackground(COLOR_HP_RED); }
        });

        closeShopBtn.addActionListener(click -> {
            Window win = SwingUtilities.getWindowAncestor(closeShopBtn);
            if (win != null) win.dispose(); // Custom exit frame dispatcher link
        });
        footerPanel.add(closeShopBtn, BorderLayout.CENTER);
        shopWrapper.add(footerPanel, BorderLayout.SOUTH);

        // Customize internal system palette values to enforce modal design theme profiles
        UIManager.put("OptionPane.background", COLOR_BG_PANEL);
        UIManager.put("Panel.background", COLOR_BG_PANEL);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT_LIGHT);

        JOptionPane.showOptionDialog(
                parent, 
                shopWrapper, 
                "INTER-MISSION STOCK SHOP CATALOG",
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.PLAIN_MESSAGE,
                null, 
                new Object[]{}, // Passing empty object array hides standard grey native row buttons
                null
        );

        // ===== FIXED: RETURN THE ACCURATE CLOSING BALANCE ACCOUNT METRICS =====
        return playerWallet[0];
    }

    // ===== BRAND NEW HELPER METHOD: STYLED MODAL CARD PANEL FOR WALLET DENIAL WARNINGS =====
    private static void showThemedInsufficientFundsDialog(JPanel parentPane, int currentCoins) {
        JPanel warningPanel = new JPanel(new BorderLayout(0, 12));
        warningPanel.setBackground(COLOR_BG_PANEL);
        warningPanel.setPreferredSize(new Dimension(420, 200));
        warningPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel headerLabel = new JLabel("🚫 TRANSACTION DENIED 🚫", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        headerLabel.setForeground(COLOR_HP_RED);
        warningPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel detailCard = new JPanel(new GridLayout(2, 1, 0, 4));
        detailCard.setBackground(COLOR_BG_CARD);
        detailCard.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel infoText = new JLabel("Insufficient funds in your wallet!", SwingConstants.CENTER);
        infoText.setFont(new Font("Monospaced", Font.BOLD, 13));
        infoText.setForeground(COLOR_TEXT_LIGHT);

        JPanel coinRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        coinRow.setOpaque(false);
        JLabel balancePrefix = new JLabel("Your Current Balance:");
        balancePrefix.setFont(new Font("Monospaced", Font.PLAIN, 12));
        balancePrefix.setForeground(COLOR_TEXT_MUTED);
        
        ImageIcon coinImg = loadScaledShopIcon("coin.png", 16, 16);
        JLabel coinLabel = (coinImg != null) ? new JLabel(coinImg) : new JLabel("🪙");
        
        JLabel coinsValue = new JLabel(currentCoins + " Gold Coins");
        coinsValue.setFont(new Font("Monospaced", Font.BOLD, 13));
        coinsValue.setForeground(COLOR_ACCENT_GOLD);
        coinRow.add(balancePrefix);
        coinRow.add(coinLabel);
        coinRow.add(coinsValue);

        detailCard.add(infoText);
        detailCard.add(coinRow);
        warningPanel.add(detailCard, BorderLayout.CENTER);

        JButton ackBtn = new JButton("ACKNOWLEDGE");
        ackBtn.setFont(new Font("Monospaced", Font.BOLD, 13));
        ackBtn.setBackground(new Color(69, 74, 102));
        ackBtn.setForeground(COLOR_TEXT_LIGHT);
        ackBtn.setFocusPainted(false);
        ackBtn.setPreferredSize(new Dimension(0, 38));
        ackBtn.setBorder(new LineBorder(COLOR_BORDER, 1, true));

        ackBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(ackBtn);
            if (win != null) win.dispose();
        });
        warningPanel.add(ackBtn, BorderLayout.SOUTH);

        JOptionPane.showOptionDialog(
                parentPane, warningPanel, "PURCHASE DENIED",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, new Object[]{}, null
        );
    }

    // Helper resource input loader stream scaler hook
    private static ImageIcon loadScaledShopIcon(String filename, int w, int h) {
        try {
            String primaryPath = "/com/mycompany/rpg/assets/" + filename;
            InputStream is = Shop.class.getResourceAsStream(primaryPath);
            if (is == null) {
                is = Shop.class.getResourceAsStream("/assets/" + filename);
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