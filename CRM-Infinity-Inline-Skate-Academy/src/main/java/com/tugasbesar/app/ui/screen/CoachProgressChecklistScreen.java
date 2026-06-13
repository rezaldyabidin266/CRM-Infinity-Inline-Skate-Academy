package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.ProgressTemplate;
import com.tugasbesar.app.model.StudentProgressItem;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.ProgressManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class CoachProgressChecklistScreen extends JPanel {
    private static final String LOAD_ICON = "\u2315";
    private static final String SAVE_ICON = "\u2714";

    private final User currentUser;
    private final AppModule modulePermission;
    private final ProgressManagementService service;
    private final List<User> students;
    private final List<ProgressTemplate> templates;
    private final List<StudentProgressItem> checklistRows;
    private final DefaultTableModel checklistModel;
    private final JTable checklistTable;
    private final JLabel statusLabel;
    private final JLabel formInfoLabel;
    private final JComboBox<StudentOption> studentCombo;
    private final JComboBox<TemplateOption> templateCombo;
    private ProgressAssessment selectedAssessment;

    public CoachProgressChecklistScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.service = new ProgressManagementService();
        this.students = new ArrayList<>(service.getCoachStudents(currentUser));
        this.templates = new ArrayList<>(service.getCoachProgressForms(currentUser));
        this.checklistRows = new ArrayList<>();
        this.checklistModel = new DefaultTableModel(new String[]{"Item UUID", "Kategori", "Kode Unit", "Kompetensi", "Lolos", "Checked At", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 6;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? Boolean.class : String.class;
            }
        };
        this.checklistTable = new JTable(checklistModel);
        this.statusLabel = new JLabel(" ");
        this.formInfoLabel = new JLabel("Pilih murid dan form level.");
        this.studentCombo = new JComboBox<>();
        this.templateCombo = new JComboBox<>();

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        add(buildTopStatus(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        initFilters();
    }

    private JPanel buildTopStatus() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        formInfoLabel.setAlignmentX(LEFT_ALIGNMENT);
        formInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formInfoLabel.setForeground(new Color(71, 85, 105));
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(formInfoLabel);
        return panel;
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        RoundedButton loadButton = createActionButton(LOAD_ICON + " Load", new Color(30, 64, 175), 112);
        RoundedButton saveButton = createActionButton(SAVE_ICON + " Simpan", new Color(22, 163, 74), 128);
        saveButton.setEnabled(canUpdate());

        studentCombo.addActionListener(event -> loadChecklist());
        templateCombo.addActionListener(event -> refreshStudents());
        loadButton.addActionListener(event -> loadChecklist());
        saveButton.addActionListener(event -> saveChecklist());

        filterPanel.add(new JLabel("Murid"));
        filterPanel.add(studentCombo);
        filterPanel.add(new JLabel("Form Level"));
        filterPanel.add(templateCombo);
        filterPanel.add(loadButton);
        filterPanel.add(saveButton);

        JScrollPane scrollPane = new JScrollPane(checklistTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void configureTable() {
        checklistTable.setRowHeight(34);
        checklistTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        checklistTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        checklistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        checklistTable.setGridColor(new Color(226, 232, 240));
        checklistTable.setShowVerticalLines(false);
        checklistTable.getColumnModel().getColumn(0).setMinWidth(0);
        checklistTable.getColumnModel().getColumn(0).setMaxWidth(0);
        checklistTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        checklistTable.getColumnModel().getColumn(3).setPreferredWidth(360);
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void initFilters() {
        templateCombo.removeAllItems();
        templateCombo.addItem(new TemplateOption(null));
        for (ProgressTemplate template : templates) {
            templateCombo.addItem(new TemplateOption(template));
        }
        if (templateCombo.getItemCount() > 1) {
            templateCombo.setSelectedIndex(1);
        } else {
            refreshStudents();
        }
    }

    private void refreshStudents() {
        studentCombo.removeAllItems();
        studentCombo.addItem(new StudentOption(null));
        selectedAssessment = null;
        checklistModel.setRowCount(0);

        TemplateOption option = (TemplateOption) templateCombo.getSelectedItem();
        if (option == null || option.template == null) {
            formInfoLabel.setText("Pilih form level. Murid akan difilter sesuai level form itu.");
            return;
        }
        int studentCount = 0;
        for (User student : students) {
            if (option.template.getLevelUuid() != null && option.template.getLevelUuid().equals(student.getLevelUuid())) {
                studentCombo.addItem(new StudentOption(student));
                studentCount++;
            }
        }
        if (studentCount > 0) {
            studentCombo.setSelectedIndex(1);
            formInfoLabel.setText("Form level: " + safe(option.template.getName())
                    + " | Level: " + safe(option.template.getLevelName())
                    + " | Murid tersedia: " + studentCount);
        } else {
            formInfoLabel.setText("Form level: " + safe(option.template.getName())
                    + " | Belum ada murid pada level " + safe(option.template.getLevelName()) + ".");
        }
    }

    private void loadChecklist() {
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        TemplateOption template = (TemplateOption) templateCombo.getSelectedItem();
        checklistRows.clear();
        checklistModel.setRowCount(0);
        selectedAssessment = null;
        if (student == null || student.user == null || template == null || template.template == null) {
            return;
        }

        try {
            selectedAssessment = service.getOrCreateLevelAssessment(currentUser, student.user, template.template);
            checklistRows.addAll(service.getStudentChecklist(template.template.getUuid(), student.user.getUuid(), selectedAssessment.getUuid()));
            for (StudentProgressItem row : checklistRows) {
                checklistModel.addRow(new Object[]{
                        row.getItemUuid(),
                        formatCategory(row.getCategory()),
                        row.getKodeUnit(),
                        row.getKompetensi(),
                        row.isPassed(),
                        safe(row.getCheckedAt()),
                        safe(row.getNotes())
                });
            }
            formInfoLabel.setText("Form level: " + safe(template.template.getName())
                    + " | Level murid: " + safe(student.user.getLevelName())
                    + " | Riwayat tersimpan untuk level ini.");
            setStatusSuccess("Checklist form level dimuat.");
        } catch (RuntimeException exception) {
            setStatusError(exception.getMessage());
        }
    }

    private void saveChecklist() {
        StudentOption student = (StudentOption) studentCombo.getSelectedItem();
        TemplateOption template = (TemplateOption) templateCombo.getSelectedItem();
        if (student == null || student.user == null || template == null || template.template == null) {
            setStatusError("Pilih murid dan form level terlebih dahulu.");
            return;
        }
        try {
            if (selectedAssessment == null) {
                selectedAssessment = service.getOrCreateLevelAssessment(currentUser, student.user, template.template);
            }
            List<StudentProgressItem> rows = new ArrayList<>();
            for (int i = 0; i < checklistModel.getRowCount(); i++) {
                StudentProgressItem item = new StudentProgressItem();
                item.setItemUuid(String.valueOf(checklistModel.getValueAt(i, 0)));
                item.setPassed(Boolean.TRUE.equals(checklistModel.getValueAt(i, 4)));
                Object notesValue = checklistModel.getValueAt(i, 6);
                item.setNotes(notesValue == null ? "" : String.valueOf(notesValue));
                rows.add(item);
            }
            service.saveStudentChecklist(currentUser, student.user.getUuid(), template.template.getUuid(), selectedAssessment.getUuid(), rows);
            loadChecklist();
            setStatusSuccess("Checklist form level berhasil disimpan.");
        } catch (RuntimeException exception) {
            setStatusError(exception.getMessage());
        }
    }

    private boolean canUpdate() {
        return currentUser.isSuperAdmin() || modulePermission.canUpdate();
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
        return normalized;
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
            return template == null ? "Pilih Form Level" : template.getName() + " - " + (template.getLevelName() == null ? "-" : template.getLevelName());
        }
    }
}
