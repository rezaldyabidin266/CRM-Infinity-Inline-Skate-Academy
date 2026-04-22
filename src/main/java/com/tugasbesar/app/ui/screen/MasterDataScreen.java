package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.Role;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.MasterDataService;
import com.tugasbesar.app.service.UserManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MasterDataScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EXPORT_ICON = "\u21E9";
    private static final String JASPER_ICON = "\u2399";
    private static final String IMPORT_ICON = "\u21E7";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String SEARCH_ICON = "\u2315";
    private static final String CLEAR_ICON = "\u2715";

    public enum MasterType {
        MURID,
        COACH
    }

    private final MasterDataService masterDataService;
    private final UserManagementService userManagementService;
    private final User currentUser;
    private final AppModule modulePermission;
    private final Runnable sessionRefreshAction;
    private final DefaultTableModel tableModel;
    private final JTable masterTable;
    private final JLabel statusLabel;
    private final JLabel dataInfoLabel;
    private final JTextField searchField;
    private final JComboBox<String> levelFilter;
    private final JComboBox<String> statusFilter;
    private final MasterType masterType;
    private final List<User> sourceUsers;

    public MasterDataScreen(User currentUser, AppModule modulePermission, MasterType masterType, Runnable sessionRefreshAction) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.sessionRefreshAction = sessionRefreshAction;
        this.masterType = masterType;
        this.masterDataService = new MasterDataService();
        this.userManagementService = new UserManagementService();
        this.sourceUsers = new ArrayList<>();
        this.tableModel = new DefaultTableModel(
                new String[]{"UUID", "Full Name", "Username", "Email", "Role", "Level", "Status", "Last Login", "Action"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.masterTable = new JTable(tableModel);
        this.statusLabel = new JLabel(" ");
        this.dataInfoLabel = new JLabel("Data ditampilkan: 0");
        this.searchField = new JTextField();
        this.levelFilter = new JComboBox<>(new String[]{"Semua Level"});
        this.statusFilter = new JComboBox<>(new String[]{"Semua Status", "Active", "Inactive"});

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        add(buildTopSection(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(masterTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        infoPanel.setOpaque(false);
        dataInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dataInfoLabel.setForeground(new Color(71, 85, 105));
        infoPanel.add(dataInfoLabel);
        panel.add(infoPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTopSection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        RoundedButton addButton = createActionButton(ADD_ICON + " Tambah", new Color(14, 116, 144), 132);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 142);
        RoundedButton exportButton = createActionButton(EXPORT_ICON + " Export", new Color(2, 132, 199), 154);
        RoundedButton jasperButton = createActionButton(JASPER_ICON + " Jasper PDF", new Color(29, 78, 216), 176);
        RoundedButton importButton = createActionButton(IMPORT_ICON + " Import", new Color(22, 163, 74), 154);
        RoundedButton searchButton = createActionButton(SEARCH_ICON + " Search", new Color(30, 64, 175), 126);
        RoundedButton clearButton = createActionButton(CLEAR_ICON + " Clear", new Color(100, 116, 139), 126);

        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> openUserDialog(null));
        refreshButton.addActionListener(event -> loadData());
        exportButton.setEnabled(canExport());
        exportButton.addActionListener(event -> exportData());
        jasperButton.setEnabled(canExport());
        jasperButton.addActionListener(event -> exportJasperPdf());
        importButton.setEnabled(canImport());
        importButton.addActionListener(event -> importData());
        searchButton.addActionListener(event -> applyFilters());
        clearButton.addActionListener(event -> {
            searchField.setText("Cari nama, username, email, role...");
            levelFilter.setSelectedIndex(0);
            statusFilter.setSelectedIndex(0);
            applyFilters();
        });

        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(exportButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(jasperButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(importButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        searchField.setPreferredSize(new Dimension(280, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Cari berdasarkan nama, username, email, role, atau level");
        searchField.setText("Cari nama, username, email, role...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if ("Cari nama, username, email, role...".equals(searchField.getText())) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                    searchField.setText("Cari nama, username, email, role...");
                }
            }
        });
        searchField.addActionListener(event -> applyFilters());
        levelFilter.setPreferredSize(new Dimension(180, 34));
        levelFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        levelFilter.addActionListener(event -> applyFilters());
        statusFilter.setPreferredSize(new Dimension(140, 34));
        statusFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusFilter.addActionListener(event -> applyFilters());

        filterPanel.add(new JLabel("Search"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);
        filterPanel.add(new JLabel("Level"));
        filterPanel.add(levelFilter);
        filterPanel.add(new JLabel("Status"));
        filterPanel.add(statusFilter);

        wrapper.add(actionPanel);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(filterPanel);
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(statusLabel);
        return wrapper;
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void configureTable() {
        masterTable.setRowHeight(34);
        masterTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        masterTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        masterTable.getTableHeader().setReorderingAllowed(true);
        masterTable.setAutoCreateRowSorter(true);
        masterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        masterTable.setGridColor(new Color(226, 232, 240));
        masterTable.setShowVerticalLines(false);
        masterTable.setShowHorizontalLines(true);
        masterTable.getColumnModel().getColumn(0).setMinWidth(0);
        masterTable.getColumnModel().getColumn(0).setMaxWidth(0);
        masterTable.getColumnModel().getColumn(0).setWidth(0);
        masterTable.getColumnModel().getColumn(8).setPreferredWidth(170);
        masterTable.getColumnModel().getColumn(8).setCellRenderer(new ActionCellRenderer());
        masterTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = masterTable.rowAtPoint(event.getPoint());
                int viewColumn = masterTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 8) {
                    return;
                }

                int modelRow = masterTable.convertRowIndexToModel(viewRow);
                User selected = findUserByUuid(String.valueOf(tableModel.getValueAt(modelRow, 0)));
                if (selected == null) {
                    return;
                }

                int cellX = masterTable.getCellRect(viewRow, viewColumn, false).x;
                int cellWidth = masterTable.getCellRect(viewRow, viewColumn, false).width;
                int relativeX = event.getX() - cellX;
                if (relativeX < cellWidth / 2) {
                    if (!canUpdate()) {
                        showStatus("Anda tidak punya izin update data.", true);
                        return;
                    }
                    openUserDialog(selected);
                    return;
                }
                if (!canDelete()) {
                    showStatus("Anda tidak punya izin delete data.", true);
                    return;
                }
                deleteUser(selected);
            }
        });
    }

    private void loadData() {
        List<User> coachUsers = masterDataService.getMasterCoachUsers();
        List<User> muridUsers = masterDataService.getMasterMuridUsers();

        sourceUsers.clear();
        sourceUsers.addAll(masterType == MasterType.MURID ? muridUsers : coachUsers);
        reloadLevelFilter();
        applyFilters();
    }

    private void reloadLevelFilter() {
        List<String> levels = new ArrayList<>();
        for (User user : sourceUsers) {
            String level = user.getLevelName() == null ? "" : user.getLevelName().trim();
            if (!level.isEmpty() && !levels.contains(level)) {
                levels.add(level);
            }
        }
        levelFilter.removeAllItems();
        levelFilter.addItem("Semua Level");
        for (String level : levels) {
            levelFilter.addItem(level);
        }
    }

    private void applyFilters() {
        tableModel.setRowCount(0);
        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        if ("cari nama, username, email, role...".equals(search)) {
            search = "";
        }
        String selectedLevel = String.valueOf(levelFilter.getSelectedItem());
        String selectedStatus = String.valueOf(statusFilter.getSelectedItem());

        for (User user : sourceUsers) {
            String role = safe(user.getRole());
            String level = safe(user.getLevelName());
            String status = user.isActive() ? "Active" : "Inactive";
            String blob = (safe(user.getFullName()) + " " + safe(user.getUsername()) + " " + safe(user.getEmail()) + " " + role + " " + level).toLowerCase();

            if (!search.isEmpty() && !blob.contains(search)) {
                continue;
            }
            if (!"Semua Level".equals(selectedLevel) && !selectedLevel.equalsIgnoreCase(level)) {
                continue;
            }
            if (!"Semua Status".equals(selectedStatus) && !selectedStatus.equalsIgnoreCase(status)) {
                continue;
            }

            String lastLogin = user.getLastLoginAt() == null ? "-" : user.getLastLoginAt().format(DATE_TIME_FORMATTER);
            tableModel.addRow(new Object[]{
                    user.getUuid(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getEmail(),
                    role,
                    level.isEmpty() ? "-" : level,
                    status,
                    lastLogin,
                    ""
            });
        }
        dataInfoLabel.setText("Data ditampilkan: " + tableModel.getRowCount());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private User findUserByUuid(String uuid) {
        for (User user : sourceUsers) {
            if (uuid.equals(user.getUuid())) {
                return user;
            }
        }
        return null;
    }

    private void openUserDialog(User editingUser) {
        List<Role> roles = getRolesForType();
        if (roles.isEmpty()) {
            showStatus("Role untuk kategori master ini belum tersedia.", true);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingUser == null ? "Tambah User" : "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JTextField fullNameField = createTextField();
        JTextField usernameField = createTextField();
        JTextField emailField = createTextField();
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<Role> roleCombo = new JComboBox<>(roles.toArray(new Role[0]));
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JComboBox<Level> levelCombo = new JComboBox<>(userManagementService.getAllLevels().toArray(new Level[0]));
        levelCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        levelCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        levelCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        levelCombo.setEditable(true);
        JCheckBox activeCheck = new JCheckBox("User aktif");
        activeCheck.setOpaque(false);
        activeCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noteLabel = new JLabel(" ");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (editingUser != null) {
            fullNameField.setText(editingUser.getFullName());
            usernameField.setText(editingUser.getUsername());
            emailField.setText(editingUser.getEmail());
            activeCheck.setSelected(editingUser.isActive());
            selectRole(roleCombo, editingUser.getRoleUuid());
            selectLevel(levelCombo, editingUser.getLevelUuid(), editingUser.getLevelName());
            noteLabel.setForeground(new Color(71, 85, 105));
            noteLabel.setText("Kosongkan password jika tidak ingin mengubah password.");
        } else {
            activeCheck.setSelected(true);
            noteLabel.setForeground(new Color(71, 85, 105));
            noteLabel.setText("Lengkapi data pengguna sesuai kategori master.");
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
        formPanel.add(roleCombo);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Level"));
        formPanel.add(levelCombo);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(activeCheck);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(noteLabel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        RoundedButton cancelButton = createActionButton("\u2715 Batal", new Color(100, 116, 139), 122);
        RoundedButton saveButton = createActionButton(
                editingUser == null ? "\u2714 Simpan" : "\u27F3 Update",
                new Color(14, 116, 144),
                122
        );
        cancelButton.addActionListener(event -> dialog.dispose());
        saveButton.addActionListener(event -> {
            try {
                Role role = (Role) roleCombo.getSelectedItem();
                String levelName = resolveLevelInput(levelCombo);
                if (editingUser == null) {
                    userManagementService.createUser(
                            fullNameField.getText(),
                            usernameField.getText(),
                            emailField.getText(),
                            new String(passwordField.getPassword()),
                            role == null ? null : role.getUuid(),
                            levelName,
                            activeCheck.isSelected());
                } else {
                    userManagementService.updateUser(
                            editingUser,
                            fullNameField.getText(),
                            usernameField.getText(),
                            emailField.getText(),
                            new String(passwordField.getPassword()),
                            role == null ? null : role.getUuid(),
                            levelName,
                            activeCheck.isSelected());
                    if (currentUser != null && currentUser.getUuid().equals(editingUser.getUuid()) && sessionRefreshAction != null) {
                        sessionRefreshAction.run();
                    }
                }
                loadData();
                showStatus(editingUser == null ? "Data berhasil ditambahkan." : "Data berhasil diperbarui.", false);
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
            loadData();
            showStatus("Data master berhasil dihapus.", false);
        } catch (Exception exception) {
            showStatus(exception.getMessage(), true);
        }
    }

    private List<Role> getRolesForType() {
        List<Role> filtered = new ArrayList<>();
        for (Role role : userManagementService.getAllRoles()) {
            String name = role.getName() == null ? "" : role.getName().toLowerCase();
            if (masterType == MasterType.MURID && (name.contains("murid") || name.contains("student") || name.contains("siswa") || name.contains("trial"))) {
                filtered.add(role);
            }
            if (masterType == MasterType.COACH && (name.contains("pelatih") || name.contains("coach") || name.contains("trainer") || name.contains("instruktur"))) {
                filtered.add(role);
            }
        }
        return filtered;
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
        return selectedValue == null ? "" : selectedValue.toString().trim();
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

    private File normalizeChosenFile(JFileChooser chooser, boolean saveMode) {
        File selected = chooser.getSelectedFile();
        if (selected == null) {
            return null;
        }

        String path = selected.getPath();
        File resolved = selected;
        if (path == null || path.contains("ShellFolder:") || path.startsWith("::{")) {
            File currentDirectory = chooser.getCurrentDirectory() == null ? new File(".") : chooser.getCurrentDirectory();
            resolved = new File(currentDirectory, selected.getName());
        }

        if (saveMode) {
            String lowerName = resolved.getName().toLowerCase();
            if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".csv")) {
                resolved = appendExtension(resolved, "xlsx");
            }
        }
        return resolved;
    }

    private File appendExtension(File file, String extension) {
        String lowerName = file.getName().toLowerCase();
        if (lowerName.endsWith("." + extension.toLowerCase())) {
            return file;
        }
        File parent = file.getParentFile();
        if (parent == null) {
            return new File(file.getName() + "." + extension);
        }
        return new File(parent, file.getName() + "." + extension);
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

    private boolean canExport() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canExport();
    }

    private boolean canImport() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canImport();
    }

    private boolean canUpdate() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canUpdate();
    }

    private boolean canDelete() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canDelete();
    }

    private void exportData() {
        if (!canExport()) {
            showStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Data");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = normalizeChosenFile(chooser, true);
        if (file == null) {
            showStatus("Lokasi file export tidak valid.", true);
            return;
        }
        String name = file.getName().toLowerCase();
        try {
            if (name.endsWith(".csv")) {
                exportCsv(file);
            } else {
                if (!name.endsWith(".xlsx")) {
                    file = appendExtension(file, "xlsx");
                }
                exportXlsx(file);
            }
        } catch (Exception exception) {
            showStatus("Gagal export: " + exception.getMessage(), true);
        }
    }

    private void exportJasperPdf() {
        if (!canExport()) {
            showStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Data (Jasper PDF)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));
        chooser.setFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (file == null) {
            showStatus("Lokasi file export tidak valid.", true);
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = appendExtension(file, "pdf");
        }

        try {
            exportJasperPdf(file);
            showStatus("Export Jasper PDF berhasil.", false);
        } catch (NoClassDefFoundError error) {
            showStatus("Library Jasper belum lengkap: " + error.getMessage(), true);
        } catch (Exception exception) {
            showStatus("Gagal export Jasper PDF: " + rootErrorMessage(exception), true);
        }
    }

    private void exportJasperPdf(File file) throws JRException {
        List<MasterExportRow> rows = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            rows.add(new MasterExportRow(
                    String.valueOf(tableModel.getValueAt(row, 1)),
                    String.valueOf(tableModel.getValueAt(row, 2)),
                    String.valueOf(tableModel.getValueAt(row, 3)),
                    String.valueOf(tableModel.getValueAt(row, 4)),
                    String.valueOf(tableModel.getValueAt(row, 5)),
                    String.valueOf(tableModel.getValueAt(row, 6)),
                    String.valueOf(tableModel.getValueAt(row, 7))
            ));
        }

        String jrxml = buildMasterJasperTemplate();
        InputStream templateStream = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
    }

    private String rootErrorMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.trim().isEmpty() ? current.getClass().getSimpleName() : message;
    }

    private String buildMasterJasperTemplate() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports "
                + "http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" "
                + "name=\"master_data_report\" pageWidth=\"842\" pageHeight=\"595\" orientation=\"Landscape\" "
                + "columnWidth=\"802\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">"
                + "<field name=\"fullName\" class=\"java.lang.String\"/>"
                + "<field name=\"username\" class=\"java.lang.String\"/>"
                + "<field name=\"email\" class=\"java.lang.String\"/>"
                + "<field name=\"role\" class=\"java.lang.String\"/>"
                + "<field name=\"level\" class=\"java.lang.String\"/>"
                + "<field name=\"status\" class=\"java.lang.String\"/>"
                + "<field name=\"lastLogin\" class=\"java.lang.String\"/>"
                + "<title><band height=\"36\">"
                + "<staticText><reportElement x=\"0\" y=\"0\" width=\"802\" height=\"28\"/>"
                + "<textElement><font size=\"14\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[Master Data Export]]></text></staticText>"
                + "</band></title>"
                + "<columnHeader><band height=\"22\">"
                + buildHeaderText(0, 120, "Full Name")
                + buildHeaderText(120, 90, "Username")
                + buildHeaderText(210, 190, "Email")
                + buildHeaderText(400, 110, "Role")
                + buildHeaderText(510, 90, "Level")
                + buildHeaderText(600, 80, "Status")
                + buildHeaderText(680, 122, "Last Login")
                + "</band></columnHeader>"
                + "<detail><band height=\"20\">"
                + buildDetailTextField(0, 120, "fullName")
                + buildDetailTextField(120, 90, "username")
                + buildDetailTextField(210, 190, "email")
                + buildDetailTextField(400, 110, "role")
                + buildDetailTextField(510, 90, "level")
                + buildDetailTextField(600, 80, "status")
                + buildDetailTextField(680, 122, "lastLogin")
                + "</band></detail>"
                + "</jasperReport>";
    }

    private String buildHeaderText(int x, int width, String text) {
        return "<staticText><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"20\"/>"
                + "<textElement><font size=\"10\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[" + text + "]]></text></staticText>";
    }

    private String buildDetailTextField(int x, int width, String field) {
        return "<textField textAdjust=\"StretchHeight\"><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"18\"/>"
                + "<textElement><font size=\"9\"/></textElement>"
                + "<textFieldExpression><![CDATA[$F{" + field + "}]]></textFieldExpression></textField>";
    }

    private void importData() {
        if (!canImport()) {
            showStatus("Anda tidak punya izin import.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Master Data");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = normalizeChosenFile(chooser, false);
        if (file == null || !file.exists()) {
            showStatus("File import tidak ditemukan.", true);
            return;
        }
        String name = file.getName().toLowerCase();
        if (name.endsWith(".xlsx")) {
            importXlsx(file);
        } else if (name.endsWith(".csv")) {
            importCsv(file);
        } else {
            showStatus("Format file tidak didukung. Gunakan CSV atau XLSX.", true);
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }
        exportCsv(file);
    }

    private void exportCsv(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("full_name,username,email,role,level,status,last_login");
            writer.newLine();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 1))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 2))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 3))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 4))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 5))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 6))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 7))));
                writer.newLine();
            }
            showStatus("Export CSV berhasil.", false);
        } catch (Exception exception) {
            showStatus("Gagal export CSV: " + exception.getMessage(), true);
        }
    }

    private void importCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV File", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            showStatus("Import saat ini mendukung CSV.", true);
            return;
        }
        importCsv(file);
    }

    private void importCsv(File file) {
        int failed = 0;
        int success = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.toLowerCase().startsWith("full_name,")) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    failed++;
                    continue;
                }
                try {
                    Role role = null;
                    for (Role item : getRolesForType()) {
                        if (item.getName().equalsIgnoreCase(parts[3].trim())) {
                            role = item;
                            break;
                        }
                    }
                    if (role == null) {
                        failed++;
                        continue;
                    }

                    boolean active = !"inactive".equalsIgnoreCase(parts[5].trim());
                    String password = parts[6].trim().isEmpty() ? "123456" : parts[6].trim();
                    userManagementService.createUser(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            password,
                            role.getUuid(),
                            parts[4].trim(),
                            active);
                    success++;
                } catch (Exception exception) {
                    failed++;
                }
            }
            loadData();
            showStatus("Import selesai. Berhasil: " + success + ", gagal: " + failed, failed > 0);
        } catch (Exception exception) {
            showStatus("Gagal import CSV: " + exception.getMessage(), true);
        }
    }

    private void exportXlsx(File file) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"full_name", "username", "email", "role", "level", "status", "last_login"});
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            rows.add(new String[]{
                    String.valueOf(tableModel.getValueAt(row, 1)),
                    String.valueOf(tableModel.getValueAt(row, 2)),
                    String.valueOf(tableModel.getValueAt(row, 3)),
                    String.valueOf(tableModel.getValueAt(row, 4)),
                    String.valueOf(tableModel.getValueAt(row, 5)),
                    String.valueOf(tableModel.getValueAt(row, 6)),
                    String.valueOf(tableModel.getValueAt(row, 7))
            });
        }

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file))) {
            writeZipEntry(zip, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                            + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                            + "</Types>");
            writeZipEntry(zip, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                            + "</Relationships>");
            writeZipEntry(zip, "xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                            + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                            + "<sheets><sheet name=\"Master\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                            + "</Relationships>");
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", buildSheetXml(rows));
        } catch (Exception exception) {
            throw new RuntimeException("Gagal membuat XLSX: " + exception.getMessage(), exception);
        }
        showStatus("Export XLSX berhasil.", false);
    }

    private void importXlsx(File file) {
        int success = 0;
        int failed = 0;
        try {
            List<String[]> rows = readSheetRows(file);
            for (String[] parts : rows) {
                if (parts.length < 6) {
                    failed++;
                    continue;
                }
                if ("full_name".equalsIgnoreCase(parts[0])) {
                    continue;
                }
                try {
                    Role role = null;
                    for (Role item : getRolesForType()) {
                        if (item.getName().equalsIgnoreCase(parts[3].trim())) {
                            role = item;
                            break;
                        }
                    }
                    if (role == null) {
                        failed++;
                        continue;
                    }

                    boolean active = !"inactive".equalsIgnoreCase(parts[5].trim());
                    userManagementService.createUser(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            "123456",
                            role.getUuid(),
                            parts.length > 4 ? parts[4].trim() : "Basic",
                            active);
                    success++;
                } catch (Exception exception) {
                    failed++;
                }
            }
            loadData();
            showStatus("Import XLSX selesai. Berhasil: " + success + ", gagal: " + failed, failed > 0);
        } catch (Exception exception) {
            showStatus("Gagal import XLSX: " + exception.getMessage(), true);
        }
    }

    private String buildSheetXml(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");
        for (int r = 0; r < rows.size(); r++) {
            sb.append("<row r=\"").append(r + 1).append("\">");
            String[] cols = rows.get(r);
            for (int c = 0; c < cols.length; c++) {
                String ref = columnName(c) + (r + 1);
                sb.append("<c r=\"").append(ref).append("\" t=\"inlineStr\"><is><t>")
                        .append(escapeXml(cols[c]))
                        .append("</t></is></c>");
            }
            sb.append("</row>");
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private List<String[]> readSheetRows(File file) throws Exception {
        try (ZipFile zip = new ZipFile(file)) {
            Map<Integer, String> shared = readSharedStrings(zip);
            ZipEntry sheet = zip.getEntry("xl/worksheets/sheet1.xml");
            if (sheet == null) {
                throw new IllegalArgumentException("sheet1.xml tidak ditemukan.");
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(zip.getInputStream(sheet));
            NodeList rows = doc.getElementsByTagNameNS("*", "row");
            List<String[]> result = new ArrayList<>();
            for (int i = 0; i < rows.getLength(); i++) {
                Element row = (Element) rows.item(i);
                NodeList cells = row.getElementsByTagNameNS("*", "c");
                List<String> values = new ArrayList<>();
                int expectedCol = 0;
                for (int j = 0; j < cells.getLength(); j++) {
                    Element cell = (Element) cells.item(j);
                    String ref = cell.getAttribute("r");
                    int col = columnIndex(ref);
                    while (expectedCol < col) {
                        values.add("");
                        expectedCol++;
                    }
                    values.add(readCellValue(cell, shared));
                    expectedCol++;
                }
                result.add(values.toArray(new String[0]));
            }
            return result;
        }
    }

    private Map<Integer, String> readSharedStrings(ZipFile zip) throws Exception {
        Map<Integer, String> map = new HashMap<>();
        ZipEntry shared = zip.getEntry("xl/sharedStrings.xml");
        if (shared == null) {
            return map;
        }
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(zip.getInputStream(shared));
        NodeList texts = doc.getElementsByTagNameNS("*", "t");
        for (int i = 0; i < texts.getLength(); i++) {
            map.put(i, texts.item(i).getTextContent());
        }
        return map;
    }

    private String readCellValue(Element cell, Map<Integer, String> shared) {
        String type = cell.getAttribute("t");
        NodeList vList = cell.getElementsByTagNameNS("*", "v");
        if ("inlineStr".equals(type)) {
            NodeList tList = cell.getElementsByTagNameNS("*", "t");
            return tList.getLength() > 0 ? tList.item(0).getTextContent() : "";
        }
        if ("s".equals(type) && vList.getLength() > 0) {
            int idx = Integer.parseInt(vList.item(0).getTextContent());
            return shared.getOrDefault(idx, "");
        }
        return vList.getLength() > 0 ? vList.item(0).getTextContent() : "";
    }

    private int columnIndex(String ref) {
        int idx = 0;
        for (int i = 0; i < ref.length(); i++) {
            char ch = ref.charAt(i);
            if (!Character.isLetter(ch)) {
                break;
            }
            idx = idx * 26 + (Character.toUpperCase(ch) - 'A' + 1);
        }
        return Math.max(0, idx - 1);
    }

    private String columnName(int index) {
        StringBuilder sb = new StringBuilder();
        int value = index;
        do {
            sb.insert(0, (char) ('A' + (value % 26)));
            value = value / 26 - 1;
        } while (value >= 0);
        return sb.toString();
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws Exception {
        ZipEntry entry = new ZipEntry(name);
        zip.putNextEntry(entry);
        zip.write(content.getBytes("UTF-8"));
        zip.closeEntry();
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String csvEscape(String value) {
        String text = value == null ? "" : value;
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    public static final class MasterExportRow {
        private final String fullName;
        private final String username;
        private final String email;
        private final String role;
        private final String level;
        private final String status;
        private final String lastLogin;

        public MasterExportRow(String fullName, String username, String email, String role, String level, String status, String lastLogin) {
            this.fullName = fullName;
            this.username = username;
            this.email = email;
            this.role = role;
            this.level = level;
            this.status = status;
            this.lastLogin = lastLogin;
        }

        public String getFullName() {
            return fullName;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getLevel() {
            return level;
        }

        public String getStatus() {
            return status;
        }

        public String getLastLogin() {
            return lastLogin;
        }
    }

    private final class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144), "Edit data"));
            panel.add(createActionBadge(DELETE_ICON + " Delete", new Color(220, 38, 38), "Hapus data"));
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
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return label;
    }
}
