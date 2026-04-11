package com.tugasbesar.app.ui;

import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AuthService;
import com.tugasbesar.app.ui.screen.DashboardScreen;
import com.tugasbesar.app.ui.screen.LoginScreen;
import com.tugasbesar.app.ui.screen.RegisterScreen;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

public class ScreenManager extends JFrame {
    private static final String LOGIN_SCREEN = "login";
    private static final String REGISTER_SCREEN = "register";
    private static final String DASHBOARD_SCREEN = "dashboard";

    private final CardLayout cardLayout;
    private final JPanel rootPanel;
    private final AuthService authService;
    private JPanel currentDashboardScreen;

    public ScreenManager() {
        this.authService = new AuthService();
        this.cardLayout = new CardLayout();
        this.rootPanel = new JPanel(cardLayout);

        setTitle("PV TugasBesar");
        setSize(960, 640);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(rootPanel);

        rootPanel.add(new LoginScreen(this, authService), LOGIN_SCREEN);
        rootPanel.add(new RegisterScreen(this, authService), REGISTER_SCREEN);
    }

    public void showLoginScreen() {
        cardLayout.show(rootPanel, LOGIN_SCREEN);
        revalidate();
        repaint();
    }

    public void showRegisterScreen() {
        cardLayout.show(rootPanel, REGISTER_SCREEN);
        revalidate();
        repaint();
    }

    public void showDashboardScreen(User user) {
        DashboardScreen dashboardScreen = new DashboardScreen(this, user);
        if (currentDashboardScreen != null) {
            rootPanel.remove(currentDashboardScreen);
        }
        currentDashboardScreen = dashboardScreen;
        rootPanel.add(dashboardScreen, DASHBOARD_SCREEN);
        cardLayout.show(rootPanel, DASHBOARD_SCREEN);
        revalidate();
        repaint();
    }
}
