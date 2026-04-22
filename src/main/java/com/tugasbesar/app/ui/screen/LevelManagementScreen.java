package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.LevelManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
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

public class LevelManagementScreen extends JPanel {
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String CANCEL_ICON = "\u2715";
    private static final String SAVE_ICON = "\u2714";
    private static final String UPDATE_ICON = "\u27F3";
    private static final String SEARCH_ICON = "\u2315";
    private static final String CLEAR_ICON = "\u2715";

    private final User currentUser;
    private final AppModule modulePermission;
    private final LevelManagementService levelManagementService;
    private final List<Level> levels;
    private final DefaultTableModel tableModel;
    private final JTable levelTable;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final JLabel statusLabel;
    private final JTextField searchField;

    public LevelManagementScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.levelManagementService = new LevelManagementService();
        this.levels = new ArrayList<>();
        this.tableModel = createTableModel();
        this.levelTable = new JTable(tableModel);
        this.rowSorter = new TableRowSorter<>(tableModel);
        this.statusLabel = new JLabel(" ");
        this.searchField = new JTextField();

        setLayout(new BorderLayout(0, 6));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        loadLevels();
    }

    private JPanel buildToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setOpaque(false);
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton addButton = createActionButton(ADD_ICON + " Tambah Level", new Color(14, 116, 144), 168);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 144);

        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> {
            if (!canCreate()) {
                showStatus("Anda tidak punya izin create level.", true);
                return;
            }
            openLevelDialog(null);
        });
        refreshButton.addActionListener(event -> {
            loadLevels();
            showStatus("Data level berhasil dimuat ulang.", false);
        });

        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Cari nama atau deskripsi level");
        searchField.setText("Cari level...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if ("Cari level...".equals(searchField.getText())) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                    searchField.setText("Cari level...");
                }
            }
        });
        searchField.addActionListener(event -> applySearchFilter());
        actionPanel.add(searchField);
        actionPanel.add(Box.createHorizontalStrut(8));
        RoundedButton searchButton = createActionButton(SEARCH_ICON + " Search", new Color(30, 64, 175), 122);
        searchButton.addActionListener(event -> applySearchFilter());
        actionPanel.add(searchButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        RoundedButton clearButton = createActionButton(CLEAR_ICON + " Clear", new Color(100, 116, 139), 122);
        clearButton.addActionListener(event -> {
            searchField.setText("Cari level...");
            rowSorter.setRowFilter(null);
        });
        actionPanel.add(clearButton);

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
        JScrollPane scrollPane = new JScrollPane(levelTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new String[]{"UUID", "Name", "Description", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configureTable() {
        levelTable.setRowHeight(34);
        levelTable.setRowSorter(rowSorter);
        levelTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        levelTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        levelTable.getTableHeader().setReorderingAllowed(false);
        levelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        levelTable.setGridColor(new Color(226, 232, 240));
        levelTable.setShowVerticalLines(false);
        levelTable.getColumnModel().getColumn(0).setMinWidth(0);
        levelTable.getColumnModel().getColumn(0).setMaxWidth(0);
        levelTable.getColumnModel().getColumn(0).setWidth(0);
        levelTable.getColumnModel().getColumn(3).setPreferredWidth(170);
        levelTable.getColumnModel().getColumn(3).setCellRenderer(new ActionCellRenderer());

        levelTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = levelTable.rowAtPoint(event.getPoint());
                int viewColumn = levelTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 3) {
                    return;
                }
                int row = levelTable.convertRowIndexToModel(viewRow);

                Level selected = findLevelByUuid(String.valueOf(tableModel.getValueAt(row, 0)));
                if (selected == null) {
                    return;
                }

                int cellX = levelTable.getCellRect(viewRow, viewColumn, false).x;
                int cellWidth = levelTable.getCellRect(viewRow, viewColumn, false).width;
                int relativeX = event.getX() - cellX;
                if (relativeX < cellWidth / 2) {
                    if (!canUpdate()) {
                        showStatus("Anda tidak punya izin update level.", true);
                        return;
                    }
                    openLevelDialog(selected);
                } else {
                    if (!canDelete()) {
                        showStatus("Anda tidak punya izin delete level.", true);
                        return;
                    }
                    deleteLevel(selected);
                }
            }
        });
    }

    private void loadLevels() {
        levels.clear();
        levels.addAll(levelManagementService.getAllLevels());
        tableModel.setRowCount(0);
        for (Level level : levels) {
            tableModel.addRow(new Object[]{level.getUuid(), level.getName(), level.getDescription(), ""});
        }
    }

    private void applySearchFilter() {
        String text = searchField.getText();
        String rawKeyword = text == null ? "" : text.trim().toLowerCase();
        if ("cari level...".equals(rawKeyword)) {
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
                for (int col = 1; col <= 2; col++) {
                    Object value = entry.getValue(col);
                    if (value != null && value.toString().toLowerCase().contains(keyword)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private Level findLevelByUuid(String uuid) {
        for (Level level : levels) {
            if (level.getUuid().equals(uuid)) {
                return level;
            }
        }
        return null;
    }

    private void openLevelDialog(Level editingLevel) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editingLevel == null ? "Tambah Level" : "Edit Level", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JTextField nameField = createTextField();
        JTextArea descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(360, 96));
        descriptionScroll.setMaximumSize(new Dimension(360, 96));
        descriptionScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noteLabel = new JLabel(" ");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteLabel.setForeground(new Color(71, 85, 105));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (editingLevel != null) {
            nameField.setText(editingLevel.getName());
            descriptionArea.setText(editingLevel.getDescription());
            noteLabel.setText("Perbarui nama dan deskripsi level.");
        } else {
            noteLabel.setText("Buat level baru yang bisa dipakai di user.");
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
        formPanel.add(noteLabel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        RoundedButton cancelButton = createActionButton(CANCEL_ICON + " Batal", new Color(100, 116, 139), 122);
        RoundedButton saveButton = createActionButton(
                editingLevel == null ? SAVE_ICON + " Simpan" : UPDATE_ICON + " Update",
                new Color(14, 116, 144),
                122
        );

        cancelButton.addActionListener(event -> dialog.dispose());
        saveButton.addActionListener(event -> {
            try {
                if (editingLevel == null) {
                    levelManagementService.createLevel(nameField.getText(), descriptionArea.getText());
                } else {
                    levelManagementService.updateLevel(editingLevel, nameField.getText(), descriptionArea.getText());
                }
                loadLevels();
                showStatus(editingLevel == null ? "Level berhasil ditambahkan." : "Level berhasil diperbarui.", false);
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
        dialog.setSize(470, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void deleteLevel(Level level) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus level " + level.getName() + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            levelManagementService.deleteLevel(level);
            loadLevels();
            showStatus("Level berhasil dihapus.", false);
        } catch (Exception exception) {
            showStatus(exception.getMessage(), true);
        }
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

    private final class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144), "Edit level"));
            panel.add(createActionBadge(DELETE_ICON + " Delete", new Color(220, 38, 38), "Hapus level"));
            return panel;
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
}
