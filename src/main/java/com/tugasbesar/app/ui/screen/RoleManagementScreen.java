package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.RoleModulePermission;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.RoleManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RoleManagementScreen extends JPanel {
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String CANCEL_ICON = "\u2715";
    private static final String UPDATE_ICON = "\u27F3";
    private static final String SAVE_ICON = "\u2714";
    private static final String SEARCH_ICON = "\u2315";

    private final Runnable sessionRefreshAction;
    private final User currentUser;
    private final AppModule modulePermission;
    private final RoleManagementService roleManagementService;
    private final List<Role> roles;
    private final List<AppModule> modules;
    private final DefaultTableModel tableModel;
    private final JTable roleTable;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final JLabel statusLabel;
    private final JTextField searchField;
    private Role selectedRole;

    public RoleManagementScreen(User currentUser, AppModule modulePermission, Runnable sessionRefreshAction) {
        this.sessionRefreshAction = sessionRefreshAction;
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.roleManagementService = new RoleManagementService();
        this.roles = new ArrayList<>(roleManagementService.getAllRoles());
        this.modules = new ArrayList<>(roleManagementService.getAllModules());
        this.tableModel = createTableModel();
        this.roleTable = new JTable(tableModel);
        this.rowSorter = new TableRowSorter<>(tableModel);
        this.statusLabel = new JLabel(" ");
        this.searchField = new JTextField();

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureRoleTable();
        fillRoleTable();

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildRolePanel(), BorderLayout.CENTER);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton addButton = createActionButton(ADD_ICON + " Tambah Role", new Color(14, 116, 144), 164);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 144);

        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> {
            if (!canCreate()) {
                showStatus("Anda tidak punya izin create role.", true);
                return;
            }
            openRoleDialog(null);
        });
        refreshButton.addActionListener(event -> {
            reloadRoles();
            showStatus("Data role berhasil dimuat ulang.", false);
        });

        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Cari nama role, deskripsi, atau jumlah module");
        searchField.setText("Cari role...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if ("Cari role...".equals(searchField.getText())) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                    searchField.setText("Cari role...");
                }
            }
        });
        searchField.addActionListener(event -> applySearchFilter());
        actionPanel.add(searchField);
        actionPanel.add(Box.createHorizontalStrut(8));
        RoundedButton searchButton = createActionButton(SEARCH_ICON + " Search", new Color(30, 64, 175), 122);
        searchButton.addActionListener(event -> applySearchFilter());
        actionPanel.add(searchButton);

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

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new String[]{"Name", "Description", "Modules", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel buildRolePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(roleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void configureRoleTable() {
        roleTable.setRowHeight(34);
        roleTable.setRowSorter(rowSorter);
        roleTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        roleTable.getTableHeader().setReorderingAllowed(false);
        roleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roleTable.setGridColor(new Color(226, 232, 240));
        roleTable.setShowVerticalLines(false);
        roleTable.getColumnModel().getColumn(3).setPreferredWidth(170);
        roleTable.getColumnModel().getColumn(3).setCellRenderer(new ActionCellRenderer());
        roleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = roleTable.rowAtPoint(event.getPoint());
                int viewColumn = roleTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 3) {
                    return;
                }
                int row = roleTable.convertRowIndexToModel(viewRow);

                Role role = findRoleByName(String.valueOf(tableModel.getValueAt(row, 0)));
                if (role == null) {
                    return;
                }

                int cellX = roleTable.getCellRect(viewRow, viewColumn, false).x;
                int cellWidth = roleTable.getCellRect(viewRow, viewColumn, false).width;
                int relativeX = event.getX() - cellX;
                if (relativeX < cellWidth / 2) {
                    if (!canUpdate()) {
                        showStatus("Anda tidak punya izin update role.", true);
                        return;
                    }
                    selectedRole = role;
                    openRoleDialog(role);
                    return;
                }

                if (!canDelete()) {
                    showStatus("Anda tidak punya izin delete role.", true);
                    return;
                }
                selectedRole = role;
                deleteSelectedRole();
            }
        });
    }

    private void fillRoleTable() {
        tableModel.setRowCount(0);
        for (Role role : roles) {
            int moduleCount = roleManagementService.getModuleCodesByRole(role.getUuid()).size();
            tableModel.addRow(new Object[]{role.getName(), role.getDescription(), moduleCount + " modules", ""});
        }
    }

    private void applySearchFilter() {
        String text = searchField.getText();
        String rawKeyword = text == null ? "" : text.trim().toLowerCase();
        if ("cari role...".equals(rawKeyword)) {
            rawKeyword = "";
        }
        final String keyword = rawKeyword;
        if (keyword.isEmpty()) {
            rowSorter.setRowFilter(null);
            return;
        }
        rowSorter.setRowFilter(new javax.swing.RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                for (int col = 0; col <= 2; col++) {
                    Object value = entry.getValue(col);
                    if (value != null && value.toString().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void reloadRoles() {
        roles.clear();
        roles.addAll(roleManagementService.getAllRoles());
        fillRoleTable();
        selectedRole = null;
    }

    private void openRoleDialog(Role editingRole) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingRole == null ? "Tambah Role" : "Edit Role", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(360, 42));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(360, 96));
        descriptionScroll.setMaximumSize(new Dimension(360, 96));
        descriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        DefaultTableModel moduleTableModel = new DefaultTableModel(new String[]{"Akses", "Module", "View", "Create", "Update", "Delete", "Export", "Import"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? String.class : Boolean.class;
            }
        };

        Set<String> selectedModuleCodes = editingRole == null ? null : roleManagementService.getModuleCodesByRole(editingRole.getUuid());
        Map<String, RoleModulePermission> selectedPermissions = editingRole == null ? null : roleManagementService.getModulePermissionsByRole(editingRole.getUuid());
        for (AppModule module : modules) {
            RoleModulePermission permission = selectedPermissions == null ? null : selectedPermissions.get(module.getCode());
            boolean checked = selectedModuleCodes != null && selectedModuleCodes.contains(module.getCode());
            moduleTableModel.addRow(new Object[]{
                    checked,
                    module.getName(),
                    permission != null && permission.canView(),
                    permission != null && permission.canCreate(),
                    permission != null && permission.canUpdate(),
                    permission != null && permission.canDelete(),
                    permission != null && permission.canExport(),
                    permission != null && permission.canImport()
            });
        }

        JTable moduleTable = new JTable(moduleTableModel);
        moduleTable.setRowHeight(28);
        moduleTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        moduleTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        moduleTable.getColumnModel().getColumn(0).setPreferredWidth(58);
        moduleTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        centerPermissionColumns(moduleTable);
        JScrollPane moduleScroll = new JScrollPane(moduleTable);
        moduleScroll.setPreferredSize(new Dimension(740, 240));
        moduleScroll.setMaximumSize(new Dimension(740, 240));
        moduleScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        configurePermissionHeader(moduleTable, moduleTableModel);

        JLabel noteLabel = new JLabel(" ");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteLabel.setForeground(new Color(71, 85, 105));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (editingRole != null) {
            nameField.setText(editingRole.getName());
            descriptionArea.setText(editingRole.getDescription());
            noteLabel.setText("Atur nama, deskripsi, dan checklist module yang diizinkan.");
        } else {
            noteLabel.setText("Tentukan nama role dan module yang boleh diakses.");
        }

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Description"));
        formPanel.add(descriptionScroll);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createLabel("Modules Access"));
        formPanel.add(moduleScroll);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(noteLabel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        RoundedButton cancelButton = createActionButton(CANCEL_ICON + " Batal", new Color(100, 116, 139), 122);
        RoundedButton saveButton = createActionButton(
                editingRole == null ? SAVE_ICON + " Simpan" : UPDATE_ICON + " Update",
                new Color(14, 116, 144),
                122
        );

        cancelButton.addActionListener(event -> dialog.dispose());
        saveButton.addActionListener(event -> {
            List<RoleModulePermission> permissions = new ArrayList<>();
            for (int row = 0; row < moduleTableModel.getRowCount(); row++) {
                boolean checked = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 0));
                boolean canView = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 2));
                boolean canCreate = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 3));
                boolean canUpdate = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 4));
                boolean canDelete = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 5));
                boolean canExport = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 6));
                boolean canImport = Boolean.TRUE.equals(moduleTableModel.getValueAt(row, 7));

                if (checked || canView || canCreate || canUpdate || canDelete || canExport || canImport) {
                    RoleModulePermission permission = new RoleModulePermission();
                    permission.setModuleCode(modules.get(row).getCode());
                    permission.setCanView(checked || canView);
                    permission.setCanCreate(canCreate);
                    permission.setCanUpdate(canUpdate);
                    permission.setCanDelete(canDelete);
                    permission.setCanExport(canExport);
                    permission.setCanImport(canImport);
                    permissions.add(permission);
                }
            }

            try {
                if (editingRole == null) {
                    roleManagementService.createRole(UUID.randomUUID().toString(), nameField.getText(), descriptionArea.getText(), permissions);
                } else {
                    roleManagementService.updateRole(editingRole.getUuid(), nameField.getText(), descriptionArea.getText(), permissions);
                }
                reloadRoles();
                showStatus(editingRole == null ? "Role berhasil ditambahkan." : "Role berhasil diperbarui.", false);
                if (sessionRefreshAction != null) {
                    sessionRefreshAction.run();
                }
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
        dialog.setSize(820, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void deleteSelectedRole() {
        if (selectedRole == null) {
            JOptionPane.showMessageDialog(this, "Pilih satu role terlebih dahulu.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (currentUser != null && selectedRole.getUuid().equals(currentUser.getRoleUuid())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Role yang sedang dipakai akun login tidak boleh dihapus. Pindahkan akun Anda ke role lain dulu jika memang ingin menghapus role ini.",
                    "Tidak Bisa Hapus Role",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = showDeleteConfirm("Hapus role " + selectedRole.getName() + "?");

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            roleManagementService.deleteRole(selectedRole.getUuid());
            reloadRoles();
            showStatus("Role berhasil dihapus.", false);
            if (sessionRefreshAction != null) {
                sessionRefreshAction.run();
            }
        } catch (Exception exception) {
            showStatus(exception.getMessage(), true);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private Role findRoleByName(String roleName) {
        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }

    private final class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144), "Edit role"));
            panel.add(createActionBadge(DELETE_ICON + " Delete", new Color(220, 38, 38), "Hapus role"));
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

    private void configurePermissionHeader(JTable moduleTable, DefaultTableModel tableModel) {
        JTableHeader header = moduleTable.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setReorderingAllowed(false);
        header.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        moduleTable.getColumnModel().getColumn(0).setHeaderRenderer(new PermissionHeaderRenderer("Akses", tableModel, 0));
        moduleTable.getColumnModel().getColumn(1).setHeaderRenderer(new TextHeaderRenderer("Module"));
        moduleTable.getColumnModel().getColumn(2).setHeaderRenderer(new PermissionHeaderRenderer("View", tableModel, 2));
        moduleTable.getColumnModel().getColumn(3).setHeaderRenderer(new PermissionHeaderRenderer("Create", tableModel, 3));
        moduleTable.getColumnModel().getColumn(4).setHeaderRenderer(new PermissionHeaderRenderer("Update", tableModel, 4));
        moduleTable.getColumnModel().getColumn(5).setHeaderRenderer(new PermissionHeaderRenderer("Delete", tableModel, 5));
        moduleTable.getColumnModel().getColumn(6).setHeaderRenderer(new PermissionHeaderRenderer("Export", tableModel, 6));
        moduleTable.getColumnModel().getColumn(7).setHeaderRenderer(new PermissionHeaderRenderer("Import", tableModel, 7));

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int column = moduleTable.columnAtPoint(event.getPoint());
                if (column == 1 || column < 0) {
                    return;
                }

                boolean nextValue = !isWholeColumnSelected(tableModel, column);
                applyBulkSelection(tableModel, column, nextValue);
                header.repaint();
                moduleTable.repaint();
            }
        });
    }

    private void centerPermissionColumns(JTable moduleTable) {
        DefaultTableCellRenderer centeredHeader = new DefaultTableCellRenderer();
        centeredHeader.setHorizontalAlignment(SwingConstants.CENTER);
        centeredHeader.setFont(new Font("SansSerif", Font.BOLD, 13));

        for (int column = 0; column < moduleTable.getColumnCount(); column++) {
            moduleTable.getColumnModel().getColumn(column).setHeaderRenderer(centeredHeader);
            if (column != 1) {
                moduleTable.getColumnModel().getColumn(column).setCellRenderer(new CenteredCheckBoxRenderer());
            }
        }
    }

    private boolean isWholeColumnSelected(DefaultTableModel tableModel, int column) {
        if (tableModel.getRowCount() == 0) {
            return false;
        }
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (!Boolean.TRUE.equals(tableModel.getValueAt(row, column))) {
                return false;
            }
        }
        return true;
    }

    private void applyBulkSelection(DefaultTableModel tableModel, int column, boolean selected) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt(selected, row, column);
            if (column == 0 || column == 2) {
                tableModel.setValueAt(selected, row, 0);
                tableModel.setValueAt(selected, row, 2);
            }
        }
    }

    private static final class CenteredCheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        private CenteredCheckBoxRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected(Boolean.TRUE.equals(value));
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    private static final class TextHeaderRenderer extends DefaultTableCellRenderer {
        private TextHeaderRenderer(String text) {
            setText(text);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setOpaque(true);
            setBackground(new Color(241, 245, 249));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(203, 213, 225)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private final class PermissionHeaderRenderer extends JPanel implements TableCellRenderer {
        private final JLabel label;
        private final JCheckBox checkBox;
        private final DefaultTableModel tableModel;
        private final int columnIndex;

        private PermissionHeaderRenderer(String text, DefaultTableModel tableModel, int columnIndex) {
            this.tableModel = tableModel;
            this.columnIndex = columnIndex;
            this.label = new JLabel(text, SwingConstants.CENTER);
            this.checkBox = new JCheckBox();

            setOpaque(true);
            setBackground(new Color(241, 245, 249));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(203, 213, 225)));
            setLayout(new BorderLayout());

            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setForeground(new Color(15, 23, 42));

            checkBox.setOpaque(false);
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setEnabled(false);

            add(label, BorderLayout.NORTH);
            add(checkBox, BorderLayout.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            checkBox.setSelected(isWholeColumnSelected(tableModel, columnIndex));
            return this;
        }
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
