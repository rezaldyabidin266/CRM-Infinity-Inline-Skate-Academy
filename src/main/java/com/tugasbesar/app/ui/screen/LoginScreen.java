package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AuthService;
import com.tugasbesar.app.ui.component.AnimatedBackgroundPanel;
import com.tugasbesar.app.ui.component.RoundedButton;
import com.tugasbesar.app.ui.ScreenManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        card.setBackground(new Color(15, 23, 42, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248)),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(540, 640));
        card.setMaximumSize(new Dimension(560, 680));

        JLabel academyLogoLabel = createAcademyLogoLabel();
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        logoPanel.add(academyLogoLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Masuk ke Akun");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(new Color(241, 245, 249));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "<html>Silakan masuk untuk melanjutkan akses ke sistem pelayanan dan pengelolaan data.</html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(186, 230, 253));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField identityField = createTextField();
        JPasswordField passwordField = createPasswordField();
        JPanel passwordFieldWrapper = buildPasswordFieldWrapper(passwordField);

        messageLabel.setForeground(new Color(254, 202, 202));
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton loginButton = createButton("Masuk", new Color(8, 145, 178), Color.WHITE, null);

        Runnable loginAction = () -> {
            try {
                User user = authService.login(identityField.getText(), new String(passwordField.getPassword()));
                messageLabel.setText(" ");
                screenManager.showDashboardScreen(user);
            } catch (Exception exception) {
                messageLabel.setText(exception.getMessage() == null || exception.getMessage().trim().isEmpty()
                        ? "Login gagal. Silakan coba lagi."
                        : exception.getMessage());
            }
        };

        loginButton.addActionListener(event -> loginAction.run());
        identityField.addActionListener(event -> loginAction.run());
        passwordField.addActionListener(event -> loginAction.run());

        card.add(logoPanel);
        card.add(Box.createVerticalStrut(6));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(createFieldLabel("Email"));
        card.add(identityField);
        card.add(Box.createVerticalStrut(12));
        card.add(createFieldLabel("Password"));
        card.add(passwordFieldWrapper);
        card.add(Box.createVerticalStrut(10));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(4));

        return card;
    }

    private JLabel createAcademyLogoLabel() {
        String[] candidates = new String[]{
                "src/main/java/com/tugasbesar/app/assets/logo.png",
                "src/main/java/com/tugasbesar/app/assets/logo1.png"
        };

        for (String path : candidates) {
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            BufferedImage source = readTrimmedImage(file);
            if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0) {
                continue;
            }
            Image scaled = scaleToFit(source, 320, 150);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setPreferredSize(new Dimension(320, 150));
            logoLabel.setMaximumSize(new Dimension(320, 150));
            return logoLabel;
        }

        JLabel fallback = new JLabel("Infinity Inline Skate Academy");
        fallback.setFont(new Font("SansSerif", Font.BOLD, 25));
        fallback.setForeground(new Color(186, 230, 253));
        fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
        fallback.setHorizontalAlignment(SwingConstants.CENTER);
        return fallback;
    }

    private BufferedImage readTrimmedImage(File file) {
        try {
            BufferedImage source = ImageIO.read(file);
            if (source == null) {
                return null;
            }
            return trimTransparentPadding(source);
        } catch (IOException exception) {
            return null;
        }
    }

    private BufferedImage trimTransparentPadding(BufferedImage source) {
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int alpha = (source.getRGB(x, y) >> 24) & 0xFF;
                if (alpha > 8) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }
        return source.getSubimage(minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    private Image scaleToFit(BufferedImage source, int maxWidth, int maxHeight) {
        double widthScale = maxWidth / (double) source.getWidth();
        double heightScale = maxHeight / (double) source.getHeight();
        double scale = Math.min(widthScale, heightScale);

        int targetWidth = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int targetHeight = Math.max(1, (int) Math.round(source.getHeight() * scale));
        return source.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        textField.setPreferredSize(new Dimension(360, 44));
        textField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        textField.setBackground(new Color(30, 41, 59));
        textField.setForeground(new Color(241, 245, 249));
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(100, 116, 139)));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        passwordField.setPreferredSize(new Dimension(360, 44));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 17));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setBackground(new Color(30, 41, 59));
        passwordField.setForeground(new Color(241, 245, 249));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(100, 116, 139)));
        return passwordField;
    }

    private JPanel buildPasswordFieldWrapper(JPasswordField passwordField) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        wrapper.setPreferredSize(new Dimension(360, 44));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setOpaque(true);
        shell.setBackground(new Color(30, 41, 59));
        shell.setBorder(BorderFactory.createLineBorder(new Color(100, 116, 139)));
        shell.setPreferredSize(new Dimension(360, 44));

        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JButton toggleButton = new JButton(new EyeIcon());
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setOpaque(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        toggleButton.setMargin(new Insets(0, 0, 0, 0));
        toggleButton.setPreferredSize(new Dimension(44, 44));
        toggleButton.setToolTipText("Tampilkan/Sembunyikan Password");

        char defaultEchoChar = passwordField.getEchoChar();
        toggleButton.addActionListener(event -> {
            boolean showing = passwordField.getEchoChar() == (char) 0;
            passwordField.setEchoChar(showing ? defaultEchoChar : (char) 0);
            toggleButton.setIcon(showing ? new EyeIcon() : new EyeOffIcon());
        });

        shell.add(passwordField, BorderLayout.CENTER);
        shell.add(toggleButton, BorderLayout.EAST);
        wrapper.add(shell, BorderLayout.CENTER);
        return wrapper;
    }

    private static final class EyeIcon extends javax.swing.ImageIcon {
        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(226, 232, 240));
            g2.drawOval(x + 6, y + 12, 18, 10);
            g2.fillOval(x + 13, y + 15, 4, 4);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 30;
        }

        @Override
        public int getIconHeight() {
            return 30;
        }
    }

    private static final class EyeOffIcon extends javax.swing.ImageIcon {
        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(56, 189, 248));
            g2.drawOval(x + 6, y + 12, 18, 10);
            g2.fillOval(x + 13, y + 15, 4, 4);
            g2.drawLine(x + 6, y + 24, x + 24, y + 10);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 30;
        }

        @Override
        public int getIconHeight() {
            return 30;
        }
    }

    private RoundedButton createButton(String text, Color background, Color foreground, Color borderColor) {
        RoundedButton button = new RoundedButton(text, background, foreground, borderColor);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(new Color(226, 232, 240));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }
}
