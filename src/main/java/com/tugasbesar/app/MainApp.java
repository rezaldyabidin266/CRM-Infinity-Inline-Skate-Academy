package com.tugasbesar.app;

import com.tugasbesar.app.database.DatabaseMigrator;
import com.tugasbesar.app.ui.ScreenManager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            DatabaseMigrator.migrate();

            ScreenManager screenManager = new ScreenManager();
            screenManager.setVisible(true);
            screenManager.showLoginScreen();
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback ke default Swing look and feel.
        }
    }
}
