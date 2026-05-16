package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.StudentProgressItem;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.ProgressManagementService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class StudentProgressViewScreen extends JPanel {
    private final User currentUser;
    private final AppModule modulePermission;
    private final ProgressManagementService service;
    private final List<ProgressAssessment> assessments;
    private final DefaultTableModel assessmentModel;
    private final DefaultTableModel detailModel;
    private final JTable assessmentTable;
    private final JTable detailTable;
    private final JLabel infoLabel;

    public StudentProgressViewScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.service = new ProgressManagementService();
        this.assessments = new ArrayList<>();
        this.assessmentModel = new DefaultTableModel(new String[]{"UUID", "Tanggal", "Riwayat Progress", "Form Level", "Coach", "Hasil", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.detailModel = new DefaultTableModel(new String[]{"Kategori", "Kode Unit", "Kompetensi", "Status", "Checked At", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.assessmentTable = new JTable(assessmentModel);
        this.detailTable = new JTable(detailModel);
        this.infoLabel = new JLabel("Pilih riwayat progress untuk melihat detail checklist.");

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTables();
        add(buildContent(), BorderLayout.CENTER);
        loadAssessments();
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(new Color(71, 85, 105));
        top.add(infoLabel, BorderLayout.NORTH);
        top.add(new JScrollPane(assessmentTable), BorderLayout.CENTER);

        JScrollPane detailScroll = new JScrollPane(detailTable);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Detail Form Level"));

        panel.add(top, BorderLayout.NORTH);
        panel.add(detailScroll, BorderLayout.CENTER);
        return panel;
    }

    private void configureTables() {
        configureTable(assessmentTable);
        configureTable(detailTable);
        assessmentTable.getColumnModel().getColumn(0).setMinWidth(0);
        assessmentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        assessmentTable.getColumnModel().getColumn(2).setPreferredWidth(220);
        assessmentTable.getColumnModel().getColumn(6).setPreferredWidth(220);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(320);
        assessmentTable.getSelectionModel().addListSelectionListener(this::handleAssessmentSelection);
    }

    private void configureTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
    }

    private void loadAssessments() {
        assessments.clear();
        assessmentModel.setRowCount(0);
        detailModel.setRowCount(0);
        assessments.addAll(service.getAssessmentsByStudent(currentUser.getUuid(), null));
        for (ProgressAssessment assessment : assessments) {
            assessmentModel.addRow(new Object[]{
                    assessment.getUuid(),
                    safe(assessment.getAssessmentDate()),
                    safe(assessment.getAssessmentName()),
                    safe(assessment.getTemplateName()),
                    safe(assessment.getCoachName()),
                    assessment.getPassedItems() + "/" + assessment.getTotalItems(),
                    safe(assessment.getNotes())
            });
        }
        if (!assessments.isEmpty()) {
            assessmentTable.setRowSelectionInterval(0, 0);
        } else {
            infoLabel.setText("Belum ada riwayat progress untuk akun murid ini.");
        }
    }

    private void handleAssessmentSelection(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) {
            return;
        }
        int row = assessmentTable.getSelectedRow();
        detailModel.setRowCount(0);
        if (row < 0) {
            return;
        }
        String assessmentUuid = String.valueOf(assessmentModel.getValueAt(assessmentTable.convertRowIndexToModel(row), 0));
        ProgressAssessment selected = findAssessment(assessmentUuid);
        if (selected == null) {
            return;
        }
        List<StudentProgressItem> items = service.getStudentChecklist(selected.getTemplateUuid(), currentUser.getUuid(), selected.getUuid());
        for (StudentProgressItem item : items) {
            detailModel.addRow(new Object[]{
                    formatCategory(item.getCategory()),
                    safe(item.getKodeUnit()),
                    safe(item.getKompetensi()),
                    item.isPassed() ? "Lolos" : "Belum Lolos",
                    safe(item.getCheckedAt()),
                    safe(item.getNotes())
            });
        }
        infoLabel.setText("Riwayat: " + safe(selected.getAssessmentName()) + " | Tanggal: " + safe(selected.getAssessmentDate())
                + " | Template: " + safe(selected.getTemplateName()));
    }

    private ProgressAssessment findAssessment(String uuid) {
        for (ProgressAssessment assessment : assessments) {
            if (assessment.getUuid().equals(uuid)) {
                return assessment;
            }
        }
        return null;
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
}
