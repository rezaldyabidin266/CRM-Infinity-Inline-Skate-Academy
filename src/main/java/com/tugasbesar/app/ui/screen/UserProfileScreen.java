package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;

public class UserProfileScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public UserProfileScreen(User user) {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel header = new JPanel();
        header.setOpaque(true);
        header.setBackground(new Color(15, 23, 42));
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(safe(user.getFullName()));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel emailLabel = new JLabel(safe(user.getEmail()));
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(191, 219, 254));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel badgeRow = new JPanel();
        badgeRow.setOpaque(false);
        badgeRow.setLayout(new BoxLayout(badgeRow, BoxLayout.X_AXIS));
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        badgeRow.add(buildBadge("Role: " + safe(user.getRole()), new Color(22, 163, 74)));
        badgeRow.add(Box.createHorizontalStrut(8));
        badgeRow.add(buildBadge("Level: " + safe(user.getLevelName()), new Color(37, 99, 235)));
        badgeRow.add(Box.createHorizontalStrut(8));
        badgeRow.add(buildBadge("Status: " + (user.isActive() ? "Active" : "Inactive"), user.isActive() ? new Color(8, 145, 178) : new Color(220, 38, 38)));

        header.add(nameLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(emailLabel);
        header.add(Box.createVerticalStrut(12));
        header.add(badgeRow);

        JPanel content = new JPanel(new GridLayout(0, 2, 14, 14));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        String lastLogin = user.getLastLoginAt() == null ? "-" : user.getLastLoginAt().format(DATE_TIME_FORMATTER);

        content.add(buildInfoBlock("Username", safe(user.getUsername())));
        content.add(buildInfoBlock("Super Admin", user.isSuperAdmin() ? "Ya" : "Tidak"));
        content.add(buildInfoBlock("Role", safe(user.getRole())));
        content.add(buildInfoBlock("Level", safe(user.getLevelName())));
        content.add(buildInfoBlock("Status Akun", user.isActive() ? "Active" : "Inactive"));
        content.add(buildInfoBlock("Last Login", lastLogin));

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        add(card, BorderLayout.NORTH);
    }

    private JPanel buildInfoBlock(String title, String value) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(71, 85, 105));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(6));
        panel.add(valueLabel);
        return panel;
    }

    private JLabel buildBadge(String text, Color color) {
        JLabel badge = new JLabel(text);
        badge.setOpaque(true);
        badge.setBackground(color);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return badge;
    }

    private String safe(String text) {
        return text == null || text.trim().isEmpty() ? "-" : text.trim();
    }
}
