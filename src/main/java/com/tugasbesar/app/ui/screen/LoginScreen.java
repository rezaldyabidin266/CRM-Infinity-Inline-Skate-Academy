package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AuthService;
import com.tugasbesar.app.ui.component.AnimatedBackgroundPanel;
import com.tugasbesar.app.ui.component.RoundedButton;
import com.tugasbesar.app.ui.ScreenManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class LoginScreen extends JPanel {
    private final ScreenManager screenManager;
    private final AuthService authService;
    private final JLabel messageLabel;

    public LoginScreen(ScreenManager screenManager, AuthService authService) {
        this.screenManager = screenManager;
        this.authService = authService;
        this.messageLabel = new JLabel(" ");

        setLayout(new BorderLayout());

        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new java.awt.GridBagLayout());
        backgroundPanel.add(buildFormCard());

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JPanel buildFormCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(34, 36, 30, 36)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(520, 560));
        card.setMaximumSize(new Dimension(540, 580));

        JLabel academyTagLabel = new JLabel("WELCOME TO");
        academyTagLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        academyTagLabel.setForeground(new Color(14, 116, 144));
        academyTagLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel academyLabel = new JLabel("Infinity Inline Skate Academy");
        academyLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        academyLabel.setForeground(new Color(30, 64, 175));
        academyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Masuk ke Akun");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "<html>Silakan masuk untuk melanjutkan akses ke sistem pelayanan dan pengelolaan data.</html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(71, 85, 105));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField identityField = createTextField();
        JPasswordField passwordField = createPasswordField();

        messageLabel.setForeground(new Color(220, 38, 38));
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton loginButton = createButton("Masuk", new Color(14, 116, 144), Color.WHITE, null);
        RoundedButton registerButton = createButton("Buat Akun Baru", new Color(255, 255, 255), new Color(15, 23, 42),
                new Color(148, 163, 184));

        Runnable loginAction = () -> {
            try {
                User user = authService.login(identityField.getText(), new String(passwordField.getPassword()));
                messageLabel.setText(" ");
                screenManager.showDashboardScreen(user);
            } catch (Exception exception) {
                messageLabel.setText(exception.getMessage());
            }
        };

        loginButton.addActionListener(event -> loginAction.run());
        identityField.addActionListener(event -> loginAction.run());
        passwordField.addActionListener(event -> loginAction.run());

        registerButton.addActionListener(event -> screenManager.showRegisterScreen());

        card.add(academyTagLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(academyLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(createFieldLabel("Username atau Email"));
        card.add(identityField);
        card.add(Box.createVerticalStrut(16));
        card.add(createFieldLabel("Password"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(14));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(12));
        card.add(registerButton);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        textField.setPreferredSize(new Dimension(360, 44));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setPreferredSize(new Dimension(360, 44));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        return passwordField;
    }

    private RoundedButton createButton(String text, Color background, Color foreground, Color borderColor) {
        RoundedButton button = new RoundedButton(text, background, foreground, borderColor);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }
}
