package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.ProgressTemplate;
import com.tugasbesar.app.model.ProgressTemplateItem;
import com.tugasbesar.app.model.StudentProgressItem;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.ProgressManagementService;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class MasterProgressScreen extends JPanel {
    private static final String ADD_ICON = "\u271A";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String SAVE_ICON = "\u2714";

    private final User currentUser;
    private final AppModule modulePermission;
    private final ProgressManagementService service;
    private final List<Level> levels;
    private final List<User> students;
    private final List<ProgressTemplate> templates;
    private final List<ProgressTemplateItem> items;
    private final List<StudentProgressItem> checklistRows;

    private final DefaultTableModel templateModel;
    private final DefaultTableModel itemModel;
    private final DefaultTableModel checklistModel;
    private final JTable templateTable;
    private final JTable itemTable;
    private final JTable checklistTable;
    private final JLabel statusLabel;
    private final JComboBox<StudentOption> studentCombo;
    private final JComboBox<TemplateOption> checklistTemplateCombo;

    public MasterProgressScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.service = new ProgressManagementService();
        this.levels = new ArrayList<>(service.getLevels());
        this.students = new ArrayList<>(service.getCoachStudents(currentUser));
        this.templates = new ArrayList<>();
        this.items = new ArrayList<>();
        this.checklistRows = new ArrayList<>();
        this.templateModel = new DefaultTableModel(new String[]{"UUID", "Level", "Template", "Item Aktif", "Status", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.itemModel = new DefaultTableModel(new String[]{"UUID", "Kode Unit", "Kompetensi", "Kategori", "Status", "Urutan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.checklistModel = new DefaultTableModel(new String[]{"Item UUID", "Kode Unit", "Kompetensi", "Lolos", "Checked At", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }
        };
        this.templateTable = new JTable(templateModel);
        this.itemTable = new JTable(itemModel);
        this.checklistTable = new JTable(checklistModel);
        this.statusLabel = new JLabel(" ");
        this.studentCombo = new JComboBox<>();
        this.checklistTemplateCombo = new JComboBox<>();

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTables();
        add(buildTopStatus(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        loadTemplates();
    }

    private JPanel buildTopStatus() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        panel.add(statusLabel);
        return panel;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Template Progress", buildTemplateTab());
        return tabs;
    }

    private JPanel buildTemplateTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        RoundedButton addTemplate = createActionButton(ADD_ICON + " Template", new Color(14, 116, 144), 142);
        RoundedButton editTemplate = createActionButton(EDIT_ICON + " Edit Template", new Color(30, 64, 175), 164);
        RoundedButton deleteTemplate = createActionButton(DELETE_ICON + " Delete Template", new Color(220, 38, 38), 174);
        RoundedButton addItem = createActionButton(ADD_ICON + " Item", new Color(22, 163, 74), 112);
        RoundedButton editItem = createActionButton(EDIT_ICON + " Edit Item", new Color(249, 115, 22), 126);
        RoundedButton deleteItem = createActionButton(DELETE_ICON + " Delete Item", new Color(100, 116, 139), 138);
        RoundedButton refresh = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 132);

        addTemplate.setEnabled(canCreate());
        editTemplate.setEnabled(canUpdate());
        deleteTemplate.setEnabled(canDelete());
        addItem.setEnabled(canCreate());
        editItem.setEnabled(canUpdate());
        deleteItem.setEnabled(canDelete());
        addTemplate.addActionListener(event -> openTemplateDialog(null));
        editTemplate.addActionListener(event -> openTemplateDialog(getSelectedTemplate()));
        deleteTemplate.addActionListener(event -> deleteSelectedTemplate());
        addItem.addActionListener(event -> openItemDialog(null));
        editItem.addActionListener(event -> openItemDialog(getSelectedItem()));
        deleteItem.addActionListener(event -> deleteSelectedItem());
        refresh.addActionListener(event -> loadTemplates());

        actionPanel.add(addTemplate);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(editTemplate);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(deleteTemplate);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(addItem);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(editItem);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(deleteItem);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(refresh);

        JPanel tables = new JPanel(new BorderLayout(10, 0));
        tables.setOpaque(false);
        tables.add(wrapTable(templateTable), BorderLayout.WEST);
        tables.add(wrapTable(itemTable), BorderLayout.CENTER);

        panel.add(actionPanel, BorderLayout.NORTH);
        panel.add(tables, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChecklistTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        RoundedButton loadButton = createActionButton("\u2315 Load", new Color(30, 64, 175), 110);
        RoundedButton saveButton = createActionButton(SAVE_ICON + " Simpan", new Color(22, 163, 74), 128);
        saveButton.setEnabled(canUpdate());
        studentCombo.addActionListener(event -> refreshChecklistTemplates());
        checklistTemplateCombo.addActionListener(event -> loadChecklist());
        loadButton.addActionListener(event -> loadChecklist());
        saveButton.addActionListener(event -> saveChecklist());

        filterPanel.add(new JLabel("Murid"));
        filterPanel.add(studentCombo);
        filterPanel.add(new JLabel("Template"));
        filterPanel.add(checklistTemplateCombo);
        filterPanel.add(loadButton);
        filterPanel.add(saveButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(wrapTable(checklistTable), BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        scrollPane.setPreferredSize(new Dimension(520, 420));
        return scrollPane;
    }

    private void configureTables() {
        configureTable(templateTable);
        configureTable(itemTable);
        configureTable(checklistTable);
        templateTable.getColumnModel().getColumn(0).setMinWidth(0);
        templateTable.getColumnModel().getColumn(0).setMaxWidth(0);
        itemTable.getColumnModel().getColumn(0).setMinWidth(0);
        itemTable.getColumnModel().getColumn(0).setMaxWidth(0);
        checklistTable.getColumnModel().getColumn(0).setMinWidth(0);
        checklistTable.getColumnModel().getColumn(0).setMaxWidth(0);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        checklistTable.getColumnModel().getColumn(2).setPreferredWidth(360);
        templateTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadItemsForSelectedTemplate();
            }
        });
    }

    private void configureTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadTemplates() {
        templates.clear();
        templates.addAll(service.getTemplates());
        templateModel.setRowCount(0);
        for (ProgressTemplate template : templates) {
            templateModel.addRow(new Object[]{
                    template.getUuid(),
                    safe(template.getLevelName()),
                    safe(template.getName()),
                    template.getItemCount(),
                    template.isActive() ? "Aktif" : "Nonaktif",
                    safe(template.getNotes())
            });
        }
        if (!templates.isEmpty()) {
            templateTable.setRowSelectionInterval(0, 0);
        } else {
            itemModel.setRowCount(0);
        }
    }

    private void loadItemsForSelectedTemplate() {
        ProgressTemplate selected = getSelectedTemplate();
        items.clear();
        itemModel.setRowCount(0);
        if (selected == null) {
            return;
        }
        items.addAll(service.getItemsByTemplate(selected.getUuid()));
        for (ProgressTemplateItem item : items) {
            itemModel.addRow(new Object[]{
                    item.getUuid(),
                    item.getKodeUnit(),
                    item.getKompetensi(),
                    formatCategory(item.getCategory()),
                    item.isActive() ? "Aktif" : "Nonaktif",
                    item.getSortOrder()
            });
        }
    }

    private void initChecklistFilters() {
        studentCombo.removeAllItems();
        studentCombo.addItem(new StudentOption(null));
        for (User student : students) {
            studentCombo.addItem(new StudentOption(student));
        }
        refreshChecklistTemplates();
    }

    private void refreshChecklistTemplates() {
        checklistTemplateCombo.removeAllItems();
        checklistTemplateCombo.addItem(new TemplateOption(null));
        StudentOption option = (StudentOption) studentCombo.getSelectedItem();
        if (option == null || option.user == null) {
            checklistModel.setRowCount(0);
            return;
        }
        List<ProgressTemplate> activeTemplates = service.getActiveTemplatesByLevel(option.user.getLevelUuid());
        for (ProgressTemplate template : activeTemplates) {
            checklistTemplateCombo.addItem(new TemplateOption(template));
        }
        if (checklistTemplateCombo.getItemCount() > 1) {
            checklistTemplateCombo.setSelectedIndex(1);
        } else {
            checklistModel.setRowCount(0);
        }
    }

    private void loadChecklist() {
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        TemplateOption template = (TemplateOption) checklistTemplateCombo.getSelectedItem();
        checklistRows.clear();
        checklistModel.setRowCount(0);
        if (student == null || student.user == null || template == null || template.template == null) {
            return;
        }
        List<ProgressAssessment> assessmentRows = service.getAssessmentsByStudent(student.user.getUuid(), template.template.getUuid());
        if (assessmentRows.isEmpty()) {
            return;
        }
        checklistRows.addAll(service.getStudentChecklist(template.template.getUuid(), student.user.getUuid(), assessmentRows.get(0).getUuid()));
        for (StudentProgressItem row : checklistRows) {
            checklistModel.addRow(new Object[]{
                    row.getItemUuid(),
                    row.getKodeUnit(),
                    row.getKompetensi(),
                    row.isPassed(),
                    safe(row.getCheckedAt()),
                    safe(row.getNotes())
            });
        }
    }

    private void saveChecklist() {
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        TemplateOption template = (TemplateOption) checklistTemplateCombo.getSelectedItem();
        if (student == null || student.user == null || template == null || template.template == null) {
            setStatusError("Pilih murid dan template progress terlebih dahulu.");
            return;
        }
        List<StudentProgressItem> rows = new ArrayList<>();
        for (int i = 0; i < checklistModel.getRowCount(); i++) {
            StudentProgressItem item = new StudentProgressItem();
            item.setItemUuid(String.valueOf(checklistModel.getValueAt(i, 0)));
            Object passed = checklistModel.getValueAt(i, 3);
            item.setPassed(Boolean.TRUE.equals(passed));
            item.setNotes(String.valueOf(checklistModel.getValueAt(i, 5) == null ? "" : checklistModel.getValueAt(i, 5)));
            rows.add(item);
        }
        try {
            List<ProgressAssessment> assessmentRows = service.getAssessmentsByStudent(student.user.getUuid(), template.template.getUuid());
            if (assessmentRows.isEmpty()) {
                throw new IllegalArgumentException("Belum ada riwayat progress untuk murid ini.");
            }
            service.saveStudentChecklist(currentUser, student.user.getUuid(), template.template.getUuid(), assessmentRows.get(0).getUuid(), rows);
            loadChecklist();
            setStatusSuccess("Progress murid berhasil disimpan.");
        } catch (RuntimeException exception) {
            setStatusError(exception.getMessage());
        }
    }

    private void openTemplateDialog(ProgressTemplate existing) {
        JDialog dialog = new JDialog((Frame) null, existing == null ? "Tambah Template Progress" : "Edit Template Progress", true);
        dialog.setSize(520, 330);
        dialog.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JComboBox<LevelOption> levelCombo = new JComboBox<>();
        for (Level level : levels) {
            levelCombo.addItem(new LevelOption(level));
        }
        JTextField nameField = new JTextField(existing == null ? "" : existing.getName());
        JTextArea notesArea = new JTextArea(existing == null ? "" : existing.getNotes(), 4, 24);
        JCheckBox activeCheck = new JCheckBox("Aktif", existing == null || existing.isActive());
        if (existing != null) {
            selectLevel(levelCombo, existing.getLevelUuid());
        }

        addFormRow(form, gbc, 0, "Level", levelCombo);
        addFormRow(form, gbc, 1, "Nama Template", nameField);
        addFormRow(form, gbc, 2, "Catatan", new JScrollPane(notesArea));
        addFormRow(form, gbc, 3, "Status", activeCheck);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton cancel = createActionButton("Batal", new Color(100, 116, 139), 96);
        RoundedButton save = createActionButton(SAVE_ICON + " Simpan", new Color(22, 163, 74), 118);
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            try {
                LevelOption level = (LevelOption) levelCombo.getSelectedItem();
                if (level == null || level.level == null) {
                    throw new IllegalArgumentException("Level wajib dipilih.");
                }
                if (existing == null) {
                    service.createTemplate(level.level.getUuid(), nameField.getText(), notesArea.getText(), activeCheck.isSelected());
                    setStatusSuccess("Template progress berhasil dibuat.");
                } else {
                    service.updateTemplate(existing, level.level.getUuid(), nameField.getText(), notesArea.getText(), activeCheck.isSelected());
                    setStatusSuccess("Template progress berhasil diperbarui.");
                }
                dialog.dispose();
                loadTemplates();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(), "Validasi Template", JOptionPane.WARNING_MESSAGE);
            }
        });
        buttons.add(cancel);
        buttons.add(save);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openItemDialog(ProgressTemplateItem existing) {
        ProgressTemplate template = getSelectedTemplate();
        if (template == null) {
            setStatusError("Pilih template progress terlebih dahulu.");
            return;
        }
        JDialog dialog = new JDialog((Frame) null, existing == null ? "Tambah Item Kompetensi" : "Edit Item Kompetensi", true);
        dialog.setSize(560, 340);
        dialog.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JTextField codeField = new JTextField(existing == null ? "" : existing.getKodeUnit());
        JTextArea competencyArea = new JTextArea(existing == null ? "" : existing.getKompetensi(), 4, 24);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"FISIK", "TEKNIK", "FairPlay"});
        JTextField sortField = new JTextField(existing == null ? "0" : String.valueOf(existing.getSortOrder()));
        JCheckBox activeCheck = new JCheckBox("Aktif", existing == null || existing.isActive());
        categoryCombo.setSelectedItem(existing == null ? "FISIK" : formatCategory(existing.getCategory()));

        addFormRow(form, gbc, 0, "Kode Unit", codeField);
        addFormRow(form, gbc, 1, "Kompetensi", new JScrollPane(competencyArea));
        addFormRow(form, gbc, 2, "Kategori", categoryCombo);
        addFormRow(form, gbc, 3, "Urutan", sortField);
        addFormRow(form, gbc, 4, "Status Aktif", activeCheck);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton cancel = createActionButton("Batal", new Color(100, 116, 139), 96);
        RoundedButton save = createActionButton(SAVE_ICON + " Simpan", new Color(22, 163, 74), 118);
        cancel.addActionListener(event -> dialog.dispose());
        save.addActionListener(event -> {
            try {
                if (existing == null) {
                    service.createItem(
                            template.getUuid(),
                            codeField.getText(),
                            competencyArea.getText(),
                            String.valueOf(categoryCombo.getSelectedItem()),
                            sortField.getText(),
                            activeCheck.isSelected());
                    setStatusSuccess("Item kompetensi berhasil dibuat.");
                } else {
                    service.updateItem(
                            existing,
                            codeField.getText(),
                            competencyArea.getText(),
                            String.valueOf(categoryCombo.getSelectedItem()),
                            sortField.getText(),
                            activeCheck.isSelected());
                    setStatusSuccess("Item kompetensi berhasil diperbarui.");
                }
                dialog.dispose();
                loadItemsForSelectedTemplate();
                loadTemplates();
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(), "Validasi Item", JOptionPane.WARNING_MESSAGE);
            }
        });
        buttons.add(cancel);
        buttons.add(save);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
    }

    private void deleteSelectedTemplate() {
        ProgressTemplate selected = getSelectedTemplate();
        if (selected == null) {
            setStatusError("Pilih template progress yang ingin dihapus.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus template progress ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.deleteTemplate(selected);
            setStatusSuccess("Template progress berhasil dihapus.");
            loadTemplates();
        } catch (RuntimeException exception) {
            setStatusError(exception.getMessage());
        }
    }

    private void deleteSelectedItem() {
        ProgressTemplateItem selected = getSelectedItem();
        if (selected == null) {
            setStatusError("Pilih item kompetensi yang ingin dihapus.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus item kompetensi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.deleteItem(selected);
            setStatusSuccess("Item kompetensi berhasil dihapus.");
            loadItemsForSelectedTemplate();
            loadTemplates();
        } catch (RuntimeException exception) {
            setStatusError(exception.getMessage());
        }
    }

    private ProgressTemplate getSelectedTemplate() {
        int row = templateTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String uuid = String.valueOf(templateModel.getValueAt(templateTable.convertRowIndexToModel(row), 0));
        for (ProgressTemplate template : templates) {
            if (template.getUuid().equals(uuid)) {
                return template;
            }
        }
        return null;
    }

    private ProgressTemplateItem getSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        String uuid = String.valueOf(itemModel.getValueAt(itemTable.convertRowIndexToModel(row), 0));
        for (ProgressTemplateItem item : items) {
            if (item.getUuid().equals(uuid)) {
                return item;
            }
        }
        return null;
    }

    private void selectLevel(JComboBox<LevelOption> combo, String levelUuid) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            LevelOption option = combo.getItemAt(i);
            if (option.level != null && option.level.getUuid().equals(levelUuid)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private boolean canCreate() {
        return currentUser.isSuperAdmin() || modulePermission.canCreate();
    }

    private boolean canUpdate() {
        return currentUser.isSuperAdmin() || modulePermission.canUpdate();
    }

    private boolean canDelete() {
        return currentUser.isSuperAdmin() || modulePermission.canDelete();
    }

    private void setStatusSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(22, 163, 74));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(220, 252, 231));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private void setStatusError(String message) {
        statusLabel.setText(message);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(new Color(220, 38, 38));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(254, 226, 226));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String formatCategory(String value) {
        if (value == null) {
            return "-";
        }
        String normalized = value.trim().toUpperCase();
        if ("FAIRPLAY".equals(normalized) || "FAIR PLAY".equals(normalized)) {
            return "FairPlay";
        }
        if ("FISIK".equals(normalized)) {
            return "FISIK";
        }
        if ("TEKNIK".equals(normalized)) {
            return "TEKNIK";
        }
        return safe(value);
    }

    private static final class LevelOption {
        private final Level level;

        private LevelOption(Level level) {
            this.level = level;
        }

        @Override
        public String toString() {
            return level == null ? "Pilih Level" : level.getName();
        }
    }

    private static final class StudentOption {
        private final User user;

        private StudentOption(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            if (user == null) {
                return "Pilih Murid";
            }
            return user.getFullName() + " - " + (user.getLevelName() == null ? "-" : user.getLevelName());
        }
    }

    private static final class TemplateOption {
        private final ProgressTemplate template;

        private TemplateOption(ProgressTemplate template) {
            this.template = template;
        }

        @Override
        public String toString() {
            return template == null ? "Pilih Template" : template.getName();
        }
    }
}
