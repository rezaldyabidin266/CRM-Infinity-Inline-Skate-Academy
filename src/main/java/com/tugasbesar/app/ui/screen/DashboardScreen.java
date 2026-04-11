package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.AccessControlRepository;
import com.tugasbesar.app.repository.UserRepository;
import com.tugasbesar.app.ui.ScreenManager;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardScreen extends JPanel {
    private static final Color HEADER_BACKGROUND = new Color(15, 23, 42);
    private static final Color HEADER_ACCENT = new Color(34, 197, 94);
    private static final Color NAV_BACKGROUND = new Color(30, 64, 175);
    private static final Color NAV_BORDER = new Color(30, 41, 59);
    private static final Color NAV_HOVER = new Color(8, 145, 178);
    private static final Color NAV_ACTIVE = new Color(22, 163, 74);

    private final ScreenManager screenManager;
    private final AccessControlRepository accessControlRepository;
    private final UserRepository userRepository;
    private final JPanel navMenuPanel;
    private final JPanel bodyPanel;
    private final JLabel pageTitleLabel;
    private final JLabel pageSubtitleLabel;
    private final JLabel userInfoLabel;
    private final Map<String, JButton> navButtons;
    private final Icon userProfileIcon;
    private String activeNavKey;
    private User user;

    public DashboardScreen(ScreenManager screenManager, User user) {
        this.screenManager = screenManager;
        this.user = user;
        this.accessControlRepository = new AccessControlRepository();
        this.userRepository = new UserRepository();
        this.navMenuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        this.bodyPanel = new JPanel(new BorderLayout());
        this.pageTitleLabel = new JLabel("Dashboard");
        this.pageSubtitleLabel = new JLabel("Ringkasan halaman utama.");
        this.userInfoLabel = new JLabel();
        this.navButtons = new LinkedHashMap<>();
        this.userProfileIcon = new UserProfileIcon(18, new Color(241, 245, 249));
        this.activeNavKey = "HOME";

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBodyWrapper(), BorderLayout.CENTER);

        refreshSessionState();
        showHomePage();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JPanel leftSection = new JPanel();
        leftSection.setOpaque(false);
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));

        JPanel brandPanel = buildBrandPanel();
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        navMenuPanel.setOpaque(false);
        navMenuPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftSection.add(brandPanel);
        leftSection.add(Box.createVerticalStrut(16));
        leftSection.add(navMenuPanel);

        JPanel rightSection = new JPanel();
        rightSection.setOpaque(false);
        rightSection.setLayout(new BoxLayout(rightSection, BoxLayout.X_AXIS));

        userInfoLabel.setForeground(new Color(241, 245, 249));
        userInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        userInfoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        userInfoLabel.setOpaque(true);
        userInfoLabel.setBackground(new Color(30, 41, 59));
        userInfoLabel.setIcon(userProfileIcon);
        userInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        userInfoLabel.setVerticalAlignment(SwingConstants.CENTER);
        userInfoLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        userInfoLabel.setVerticalTextPosition(SwingConstants.CENTER);
        userInfoLabel.setIconTextGap(8);
        userInfoLabel.setPreferredSize(new Dimension(170, 44));
        userInfoLabel.setMinimumSize(new Dimension(170, 44));

        RoundedButton logoutButton = new RoundedButton("\u21AA Logout", new Color(249, 115, 22), Color.WHITE, null);
        logoutButton.setPreferredSize(new Dimension(132, 44));
        logoutButton.setMaximumSize(new Dimension(132, 44));
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutButton.addActionListener(event -> screenManager.showLoginScreen());

        rightSection.add(userInfoLabel);
        rightSection.add(Box.createHorizontalStrut(12));
        rightSection.add(logoutButton);

        header.add(leftSection, BorderLayout.CENTER);
        header.add(rightSection, BorderLayout.EAST);
        return header;
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panel.setOpaque(false);

        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        logoPanel.setBackground(new Color(255, 255, 255));
        logoPanel.setPreferredSize(new Dimension(48, 48));
        logoPanel.setMaximumSize(new Dimension(48, 48));
        logoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(HEADER_ACCENT, 2),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        JLabel topMark = new JLabel("CRM", SwingConstants.CENTER);
        topMark.setFont(new Font("SansSerif", Font.BOLD, 13));
        topMark.setForeground(new Color(15, 23, 42));

        JLabel bottomMark = new JLabel("II", SwingConstants.CENTER);
        bottomMark.setFont(new Font("SansSerif", Font.BOLD, 15));
        bottomMark.setForeground(new Color(8, 145, 178));

        logoPanel.add(topMark);
        logoPanel.add(bottomMark);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("CRM Infinity Inline Skate Academy");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Customer Relationship Management");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(186, 230, 253));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        panel.add(logoPanel);
        panel.add(textPanel);
        return panel;
    }

    private JPanel buildBodyWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel introPanel = new JPanel();
        introPanel.setOpaque(false);
        introPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));

        pageTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        pageTitleLabel.setForeground(new Color(15, 23, 42));
        pageTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pageSubtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pageSubtitleLabel.setForeground(new Color(71, 85, 105));
        pageSubtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        introPanel.add(pageTitleLabel);
        introPanel.add(Box.createVerticalStrut(6));
        introPanel.add(pageSubtitleLabel);

        bodyPanel.setOpaque(false);

        wrapper.add(introPanel, BorderLayout.NORTH);
        wrapper.add(bodyPanel, BorderLayout.CENTER);
        return wrapper;
    }

    private void refreshSessionState() {
        User refreshedUser = userRepository.findByUuid(user.getUuid());
        if (refreshedUser != null) {
            if (refreshedUser.isSuperAdmin()) {
                refreshedUser.setAccessibleModules(accessControlRepository.findAllModules());
            } else {
                refreshedUser.setAccessibleModules(
                        accessControlRepository.findModulesByRoleUuid(refreshedUser.getRoleUuid()));
            }
            user = refreshedUser;
        }

        userInfoLabel.setText(user.getFullName());

        rebuildNavbar();
    }

    private void rebuildNavbar() {
        navMenuPanel.removeAll();
        navButtons.clear();

        JButton homeButton = createNavButton("HOME", createNavLabel("\u25A6", "Dashboard"));
        homeButton.addActionListener(event -> showHomePage());
        navMenuPanel.add(homeButton);

        List<AppModule> orderedModules = new ArrayList<>(user.getAccessibleModules());
        orderedModules.sort((left, right) -> Integer.compare(navOrder(left.getName()), navOrder(right.getName())));

        for (AppModule module : orderedModules) {
            if ("Dashboard".equalsIgnoreCase(module.getName())) {
                continue;
            }
            String buttonLabel;
            if ("Role".equalsIgnoreCase(module.getName())) {
                buttonLabel = "Roles";
            } else if ("User".equalsIgnoreCase(module.getName())) {
                buttonLabel = "Users";
            } else {
                buttonLabel = module.getName();
            }
            JButton button = createNavButton(module.getCode(), buildModuleLabel(module.getName(), buttonLabel));
            button.addActionListener(event -> openModule(module));
            navMenuPanel.add(button);
        }

        navMenuPanel.revalidate();
        navMenuPanel.repaint();
    }

    private int navOrder(String moduleName) {
        if (moduleName == null) {
            return 999;
        }
        if ("User".equalsIgnoreCase(moduleName)) {
            return 1;
        }
        if ("Level".equalsIgnoreCase(moduleName)) {
            return 2;
        }
        if ("Role".equalsIgnoreCase(moduleName)) {
            return 3;
        }
        if ("Laporan".equalsIgnoreCase(moduleName)) {
            return 4;
        }
        if ("Pengaturan".equalsIgnoreCase(moduleName)) {
            return 5;
        }
        return 50;
    }

    private String buildModuleLabel(String moduleName, String fallbackLabel) {
        if ("User".equalsIgnoreCase(moduleName)) {
            return createNavLabel("\uD83D\uDC65", "Users");
        }
        if ("Level".equalsIgnoreCase(moduleName)) {
            return createNavLabel("\u25C8", "Level");
        }
        if ("Role".equalsIgnoreCase(moduleName)) {
            return createNavLabel("\u2699", "Roles");
        }
        if ("Laporan".equalsIgnoreCase(moduleName)) {
            return createNavLabel("\u25A4", "Laporan");
        }
        if ("Pengaturan".equalsIgnoreCase(moduleName)) {
            return createNavLabel("\u2692", "Pengaturan");
        }
        return createNavLabel("\u25C7", fallbackLabel);
    }

    private String createNavLabel(String icon, String text) {
        return icon + "  " + text;
    }

    private JButton createNavButton(String key, String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("Dialog", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBackground(NAV_BACKGROUND);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(148, 42));
        button.setMaximumSize(new Dimension(148, 42));
        button.setMinimumSize(new Dimension(148, 42));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(0, 12, 0, 12));
        button.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NAV_BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (!key.equalsIgnoreCase(activeNavKey)) {
                    button.setBackground(NAV_HOVER);
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(165, 243, 252)),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (!key.equalsIgnoreCase(activeNavKey)) {
                    button.setBackground(NAV_BACKGROUND);
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(NAV_BORDER),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                }
            }
        });
        navButtons.put(key, button);
        return button;
    }

    private void highlightNav(String selectedKey) {
        activeNavKey = selectedKey;
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equalsIgnoreCase(selectedKey);
            JButton button = entry.getValue();
            button.setBackground(active ? NAV_ACTIVE : NAV_BACKGROUND);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(active ? new Color(134, 239, 172) : NAV_BORDER),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        }
    }

    private void showHomePage() {
        setHeader("Dashboard", " ");

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        setBody(content);
        highlightNav("HOME");
    }

    private void openModule(AppModule module) {
        if ("User".equalsIgnoreCase(module.getName())) {
            setHeader("Users", "Kelola akun pengguna, status aktif, dan penetapan role.");
            setBody(new UserManagementScreen(user, module, this::handleSessionRefresh));
            highlightNav(module.getCode());
            return;
        }

        if ("Role".equalsIgnoreCase(module.getName())) {
            setHeader("Roles", "Kelola data role dan atur module yang diizinkan untuk setiap role.");
            setBody(new RoleManagementScreen(user, module, this::handleSessionRefresh));
            highlightNav(module.getCode());
            return;
        }

        if ("Level".equalsIgnoreCase(module.getName())) {
            setHeader("Levels", "Kelola level user untuk kebutuhan pembinaan dan klasifikasi.");
            setBody(new LevelManagementScreen(user, module));
            highlightNav(module.getCode());
            return;
        }

        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        placeholder.setBorder(BorderFactory.createEmptyBorder(18, 0, 0, 0));
        placeholder.setLayout(new BoxLayout(placeholder, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(module.getName());
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(15, 23, 42));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel note = new JLabel("<html>Halaman untuk module <b>" + module.getCode()
                + "</b> belum dibuat. Tapi menu ini tetap muncul karena role Anda memang punya izin ke module tersebut.</html>");
        note.setFont(new Font("SansSerif", Font.PLAIN, 14));
        note.setForeground(new Color(71, 85, 105));
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        placeholder.add(title);
        placeholder.add(Box.createVerticalStrut(10));
        placeholder.add(note);

        setHeader(module.getName(), "Informasi detail module.");
        setBody(placeholder);
        highlightNav(module.getCode());
    }

    private void handleSessionRefresh() {
        refreshSessionState();
    }

    private void setHeader(String title, String subtitle) {
        pageTitleLabel.setText(title);
        pageSubtitleLabel.setText(subtitle);
    }

    private void setBody(JPanel panel) {
        bodyPanel.removeAll();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        bodyPanel.add(scrollPane, BorderLayout.CENTER);
        bodyPanel.revalidate();
        bodyPanel.repaint();
    }

    private static final class UserProfileIcon implements Icon {
        private final int size;
        private final Color color;

        private UserProfileIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int headSize = Math.max(5, size / 2);
            int shoulderWidth = Math.max(8, size - 2);
            int shoulderHeight = Math.max(4, size / 3);

            g2.fillOval(x + (size - headSize) / 2, y, headSize, headSize);
            g2.fillRoundRect(x + (size - shoulderWidth) / 2, y + headSize - 1, shoulderWidth, shoulderHeight, 6, 6);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
