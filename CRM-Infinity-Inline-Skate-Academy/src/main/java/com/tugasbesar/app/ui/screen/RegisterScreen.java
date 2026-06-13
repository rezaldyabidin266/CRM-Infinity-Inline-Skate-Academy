package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.service.AuthService;
import com.tugasbesar.app.ui.ScreenManager;
import com.tugasbesar.app.ui.component.AnimatedBackgroundPanel;
import com.tugasbesar.app.ui.component.RoundedButton;

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
import java.awt.GridLayout;

public class RegisterScreen extends JPanel {
    private final ScreenManager screenManager;
    private final AuthService authService;
    private final JLabel messageLabel;

    public RegisterScreen(ScreenManager screenManager, AuthService authService) {
        this.screenManager = screenManager;
        this.authService = authService;
        this.messageLabel = new JLabel(" ");

        setLayout(new BorderLayout());

        AnimatedBackgroundPanel backgroundPanel = new AnimatedBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(buildLeftPanel(), BorderLayout.WEST);
        backgroundPanel.add(buildFormPanel(), BorderLayout.CENTER);

        add(backgroundPanel, BorderLayout.CENTER);
    }

    private JPanel buildLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(340, 640));
        leftPanel.setOpaque(true);
        leftPanel.setBackground(new Color(15, 23, 42, 230));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(56, 36, 56, 36));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Pendaftaran Akun");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html>Lengkapi data diri dengan benar untuk membuat akun baru. Informasi yang Anda isi digunakan sebagai identitas umum pengguna dan dapat diperbarui sesuai kebutuhan pengembangan sistem.</html>");
        subtitleLabel.setForeground(new Color(203, 213, 225));
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(16));
        leftPanel.add(subtitleLabel);

        return leftPanel;
    }

    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new GridLayout(1, 1));
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(36, 72, 36, 72));

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(540, 560));
        card.setMaximumSize(new Dimension(580, 580));

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Isi data di bawah untuk membuat akun baru.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField fullNameField = new JTextField();
        fullNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        fullNameField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        fullNameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        messageLabel.setForeground(new Color(220, 38, 38));
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton registerButton = new RoundedButton("Register", new Color(14, 116, 144), Color.WHITE, null);
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton backButton = new RoundedButton("Kembali ke Login", new Color(255, 255, 255), new Color(15, 23, 42), new Color(148, 163, 184));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        registerButton.addActionListener(event -> {
            try {
                authService.register(
                        fullNameField.getText(),
                        usernameField.getText(),
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        new String(confirmPasswordField.getPassword())
                );
                messageLabel.setText("Register berhasil. Silakan login.");
                fullNameField.setText("");
                usernameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
            } catch (Exception exception) {
                messageLabel.setText(exception.getMessage());
            }
        });

        backButton.addActionListener(event -> screenManager.showLoginScreen());

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Nama Lengkap"));
        card.add(fullNameField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("Username"));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("Email"));
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("Password"));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));
        card.add(createFieldLabel("Konfirmasi Password"));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(12));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(10));
        card.add(backButton);

        wrapper.add(card);
        return wrapper;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }
}
