package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.UserManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserManagementScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String CANCEL_ICON = "\u2715";
    private static final String UPDATE_ICON = "\u27F3";
    private static final String SAVE_ICON = "\u2714";

    private final User currentUser;
    private final AppModule modulePermission;
    private final Runnable sessionRefreshAction;
    private final UserManagementService userManagementService;
    private final List<User> users;
    private final DefaultTableModel tableModel;
    private final JTable userTable;
    private final JLabel statusLabel;

    public UserManagementScreen(User currentUser, AppModule modulePermission, Runnable sessionRefreshAction) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.sessionRefreshAction = sessionRefreshAction;
        this.userManagementService = new UserManagementService();
        this.users = new ArrayList<>();
        this.tableModel = createTableModel();
        this.userTable = new JTable(tableModel);
        this.statusLabel = new JLabel(" ");

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);

        loadUsers();
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton addButton = createActionButton(ADD_ICON + " Tambah User", new Color(14, 116, 144), 164);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 144);

        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> {
            if (!canCreate()) {
                showStatus("Anda tidak punya izin create user.", true);
                return;
            }
            openUserDialog(null);
        });
        refreshButton.addActionListener(event -> {
            loadUsers();
            showStatus("Data user berhasil dimuat ulang.", false);
        });

        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(refreshButton);

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        toolbar.add(actionPanel);
        toolbar.add(Box.createVerticalStrut(30));
        toolbar.add(statusLabel);
        return toolbar;
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new String[]{"UUID", "Full Name", "Username", "Email", "Role", "Level", "Status", "Last Login", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureTable() {
        userTable.setRowHeight(34);
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        userTable.getTableHeader().setReorderingAllowed(false);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setGridColor(new Color(226, 232, 240));
        userTable.setShowVerticalLines(false);
        userTable.setShowHorizontalLines(true);
        userTable.getColumnModel().getColumn(0).setMinWidth(0);
        userTable.getColumnModel().getColumn(0).setMaxWidth(0);
        userTable.getColumnModel().getColumn(0).setWidth(0);
        userTable.getColumnModel().getColumn(8).setPreferredWidth(170);
        userTable.getColumnModel().getColumn(8).setCellRenderer(new ActionCellRenderer());
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = userTable.rowAtPoint(event.getPoint());
                int column = userTable.columnAtPoint(event.getPoint());
                if (row < 0 || column != 8) {
                    return;
                }

                User rowUser = findUserByUuid(String.valueOf(tableModel.getValueAt(row, 0)));
                if (rowUser == null) {
                    return;
                }

                int cellX = userTable.getCellRect(row, column, false).x;
                int cellWidth = userTable.getCellRect(row, column, false).width;
                int relativeX = event.getX() - cellX;
                if (relativeX < cellWidth / 2) {
                    if (!canUpdate()) {
                        showStatus("Anda tidak punya izin update user.", true);
                        return;
                    }
                    openUserDialog(rowUser);
                    return;
                }

                if (!canDelete()) {
                    showStatus("Anda tidak punya izin delete user.", true);
                    return;
                }
                if (!rowUser.isSuperAdmin()) {
                    deleteUser(rowUser);
                }
            }
        });
    }

    private void loadUsers() {
        users.clear();
        users.addAll(userManagementService.getAllUsers());
        tableModel.setRowCount(0);

        for (User user : users) {
            String lastLogin = user.getLastLoginAt() == null ? "-" : user.getLastLoginAt().format(DATE_TIME_FORMATTER);
            tableModel.addRow(new Object[]{
                    user.getUuid(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLevelName() == null || user.getLevelName().trim().isEmpty() ? "-" : user.getLevelName(),
                    user.isActive() ? "Active" : "Inactive",
                    lastLogin,
                    ""
            });
        }

    }

    private User findUserByUuid(String userUuid) {
        for (User user : users) {
            if (user.getUuid().equals(userUuid)) {
                return user;
            }
        }
        return null;
    }

    private void openUserDialog(User editingUser) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingUser == null ? "Tambah User" : "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        List<Role> roles = userManagementService.getAllRoles();
        List<Level> levels = userManagementService.getAllLevels();
        JTextField fullNameField = createTextField();
        JTextField usernameField = createTextField();
        JTextField emailField = createTextField();
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<Role> roleComboBox = new JComboBox<>(roles.toArray(new Role[0]));
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<Level> levelComboBox = new JComboBox<>(levels.toArray(new Level[0]));
        levelComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        levelComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        levelComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        levelComboBox.setEditable(true);
        JCheckBox activeCheckBox = new JCheckBox("User aktif");
        activeCheckBox.setOpaque(false);
        activeCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noteLabel = new JLabel(" ");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (editingUser != null) {
            fullNameField.setText(editingUser.getFullName());
            usernameField.setText(editingUser.getUsername());
            emailField.setText(editingUser.getEmail());
            activeCheckBox.setSelected(editingUser.isActive());
            selectRole(roleComboBox, editingUser.getRoleUuid());
            selectLevel(levelComboBox, editingUser.getLevelUuid(), editingUser.getLevelName());
            if (editingUser.isSuperAdmin()) {
                fullNameField.setEnabled(false);
                usernameField.setEnabled(false);
                emailField.setEnabled(false);
                passwordField.setEnabled(false);
                roleComboBox.setEnabled(false);
                levelComboBox.setEnabled(false);
                activeCheckBox.setEnabled(false);
                noteLabel.setForeground(new Color(180, 83, 9));
                noteLabel.setText("Akun seeder super admin hanya bisa dilihat, tidak bisa diubah.");
            } else {
                noteLabel.setForeground(new Color(71, 85, 105));
                noteLabel.setText("Kosongkan password jika tidak ingin mengubah password.");
            }
        } else {
            activeCheckBox.setSelected(true);
            noteLabel.setForeground(new Color(71, 85, 105));
            noteLabel.setText("Lengkapi data pengguna secara lengkap dan profesional.");
        }

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createLabel("Full Name"));
        formPanel.add(fullNameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Username"));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Email"));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Role"));
        formPanel.add(roleComboBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Level"));
        formPanel.add(levelComboBox);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(activeCheckBox);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(noteLabel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        RoundedButton cancelButton = createActionButton(CANCEL_ICON + " Batal", new Color(100, 116, 139), 122);
        RoundedButton saveButton = createActionButton(
                editingUser == null ? SAVE_ICON + " Simpan" : UPDATE_ICON + " Update",
                new Color(14, 116, 144),
                122
        );

        cancelButton.addActionListener(event -> dialog.dispose());
        saveButton.addActionListener(event -> {
            try {
                Role selectedRole = (Role) roleComboBox.getSelectedItem();
                String levelName = resolveLevelInput(levelComboBox);
                if (editingUser == null) {
                    userManagementService.createUser(
                            fullNameField.getText(),
                            usernameField.getText(),
                            emailField.getText(),
                            new String(passwordField.getPassword()),
                            selectedRole == null ? null : selectedRole.getUuid(),
                            levelName,
                            activeCheckBox.isSelected()
                    );
                } else {
                    userManagementService.updateUser(
                            editingUser,
                            fullNameField.getText(),
                            usernameField.getText(),
                            emailField.getText(),
                            new String(passwordField.getPassword()),
                            selectedRole == null ? null : selectedRole.getUuid(),
                            levelName,
                            activeCheckBox.isSelected()
                    );
                    if (currentUser != null && editingUser.getUuid().equals(currentUser.getUuid()) && sessionRefreshAction != null) {
                        sessionRefreshAction.run();
                    }
                }

                loadUsers();
                showStatus(editingUser == null ? "User berhasil ditambahkan." : "User berhasil diperbarui.", false);
                dialog.dispose();
            } catch (Exception exception) {
                noteLabel.setForeground(new Color(220, 38, 38));
                noteLabel.setText(exception.getMessage());
                showStatus(exception.getMessage(), true);
            }
        });

        footer.add(cancelButton);
        footer.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(470, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void deleteUser(User user) {
        int confirm = showDeleteConfirm("Hapus pengguna " + user.getUsername() + "?");

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userManagementService.deleteUser(user, currentUser);
            loadUsers();
            showStatus("User berhasil dihapus.", false);
        } catch (Exception exception) {
            showStatus(exception.getMessage(), true);
        }
    }

    private void selectRole(JComboBox<Role> comboBox, String roleUuid) {
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            Role role = comboBox.getItemAt(index);
            if (role.getUuid().equals(roleUuid)) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    private void selectLevel(JComboBox<Level> comboBox, String levelUuid, String levelName) {
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            Level level = comboBox.getItemAt(index);
            if (levelUuid != null && levelUuid.equals(level.getUuid())) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }

        if (levelName != null && !levelName.trim().isEmpty()) {
            comboBox.getEditor().setItem(levelName.trim());
        }
    }

    private String resolveLevelInput(JComboBox<Level> comboBox) {
        Object editorValue = comboBox.isEditable() ? comboBox.getEditor().getItem() : comboBox.getSelectedItem();
        if (editorValue instanceof Level) {
            return ((Level) editorValue).getName();
        }

        if (editorValue != null) {
            String text = editorValue.toString().trim();
            if (!text.isEmpty()) {
                return text;
            }
        }

        Object selectedValue = comboBox.getSelectedItem();
        if (selectedValue instanceof Level) {
            return ((Level) selectedValue).getName();
        }
        if (selectedValue != null) {
            String text = selectedValue.toString().trim();
            if (!text.isEmpty()) {
                return text;
            }
        }
        return "";
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private final class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144), "Edit user"));
            User rowUser = findUserByUuid(String.valueOf(tableModel.getValueAt(row, 0)));
            panel.add(createActionBadge(DELETE_ICON + " Delete", rowUser != null && rowUser.isSuperAdmin() ? new Color(148, 163, 184) : new Color(220, 38, 38),
                    rowUser != null && rowUser.isSuperAdmin() ? "Akun ini tidak dapat dihapus" : "Hapus user"));
            return panel;
        }
    }

    private JLabel createActionBadge(String text, Color background, String tooltip) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(Color.WHITE);
        label.setToolTipText(tooltip);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker()),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return label;
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setText(message == null || message.trim().isEmpty() ? " " : message);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(error ? new Color(220, 38, 38) : new Color(22, 163, 74));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(error ? new Color(254, 226, 226) : new Color(220, 252, 231));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private boolean canCreate() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canCreate();
    }

    private boolean canUpdate() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canUpdate();
    }

    private boolean canDelete() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canDelete();
    }

    private int showDeleteConfirm(String message) {
        JLabel messageLabel = new JLabel("<html><b>" + message + "</b><br>Data yang dihapus tidak bisa dikembalikan.</html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        messageLabel.setPreferredSize(new Dimension(360, 76));
        return JOptionPane.showConfirmDialog(
                this,
                messageLabel,
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
    }
}
