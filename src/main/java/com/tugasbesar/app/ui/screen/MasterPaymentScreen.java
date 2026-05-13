package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.CoachPaymentSummary;
import com.tugasbesar.app.model.CoachSalaryPaymentRecord;
import com.tugasbesar.app.model.GradeCoachPaymentRate;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.LevelPaymentConfig;
import com.tugasbesar.app.model.StudentPaymentRecord;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.PaymentManagementService;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MasterPaymentScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final NumberFormat IDR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EXPORT_ICON = "\u21E9";
    private static final String PDF_ICON = "\uD83D\uDCC4";
    private static final String IMPORT_ICON = "\u21E7";

    private final User currentUser;
    private final AppModule modulePermission;
    private final Runnable sessionRefreshAction;
    private final PaymentManagementService paymentManagementService;

    private final JLabel statusLabel;
    private final DefaultTableModel levelPaymentModel;
    private final DefaultTableModel gradeCoachModel;
    private final DefaultTableModel coachSummaryModel;
    private final DefaultTableModel studentPaymentModel;
    private final DefaultTableModel coachSalaryPaymentModel;
    private final JTable studentPaymentTable;
    private final JTable coachSalaryPaymentTable;
    private final JComboBox<Integer> yearFilterCombo;
    private final JComboBox<Integer> monthFilterCombo;
    private final JComboBox<Integer> coachYearFilterCombo;
    private final JComboBox<Integer> coachMonthFilterCombo;
    private final JComboBox<LevelOption> levelFilterCombo;
    private final JComboBox<String> statusFilterCombo;
    private final JComboBox<String> coachGradeFilterCombo;
    private final JComboBox<String> coachStatusFilterCombo;
    private final List<StudentPaymentRecord> paymentRows;
    private final List<CoachSalaryPaymentRecord> coachPaymentRows;
    private final JTabbedPane paymentTabs;
    private boolean suppressPaymentEvents;
    private boolean suppressCoachPaymentEvents;

    public MasterPaymentScreen(User currentUser, AppModule modulePermission, Runnable sessionRefreshAction) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.sessionRefreshAction = sessionRefreshAction;
        this.paymentManagementService = new PaymentManagementService();
        this.statusLabel = new JLabel(" ");
        this.levelPaymentModel = new DefaultTableModel(new String[]{"UUID", "Level", "Grade", "SPP Bulanan", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.gradeCoachModel = new DefaultTableModel(new String[]{"UUID", "Grade", "Coach/Bulan", "Jumlah Coach", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.coachSummaryModel = new DefaultTableModel(new String[]{"Coach", "Grade", "Nominal/Bulan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.studentPaymentModel = new DefaultTableModel(new String[]{"UUID", "Murid", "Username", "Grade", "Level", "SPP", "Paid", "Paid At", "Catatan", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 && canUpdate();
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Boolean.class : String.class;
            }
        };
        this.studentPaymentTable = new JTable(studentPaymentModel);
        this.coachSalaryPaymentModel = new DefaultTableModel(new String[]{"UUID", "Coach", "Grade", "Nominal", "Paid", "Paid At", "Catatan", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 && canUpdate();
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? Boolean.class : String.class;
            }
        };
        this.coachSalaryPaymentTable = new JTable(coachSalaryPaymentModel);
        this.yearFilterCombo = new JComboBox<>();
        this.monthFilterCombo = new JComboBox<>();
        this.coachYearFilterCombo = new JComboBox<>();
        this.coachMonthFilterCombo = new JComboBox<>();
        this.levelFilterCombo = new JComboBox<>();
        this.statusFilterCombo = new JComboBox<>(new String[]{"Semua Status", "Sudah Bayar", "Belum Bayar"});
        this.coachGradeFilterCombo = new JComboBox<>();
        this.coachStatusFilterCombo = new JComboBox<>(new String[]{"Semua Status", "Sudah Bayar", "Belum Bayar"});
        this.paymentRows = new ArrayList<>();
        this.coachPaymentRows = new ArrayList<>();
        this.paymentTabs = new JTabbedPane();

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        initPeriodFilters();
        initLevelFilter();
        initCoachGradeFilter();
        configureStudentPaymentTable();
        configureCoachSalaryPaymentTable();
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContentSection(), BorderLayout.CENTER);
        reloadAll();
    }

    private JPanel buildToolbar() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 150);
        RoundedButton exportButton = createActionButton(EXPORT_ICON + " Export Excel", new Color(22, 163, 74), 176);
        RoundedButton pdfButton = createActionButton(PDF_ICON + " Export PDF", new Color(220, 38, 38), 164);
        RoundedButton importButton = createActionButton(IMPORT_ICON + " Import", new Color(22, 163, 74), 154);
        refreshButton.addActionListener(event -> reloadAll());
        exportButton.setEnabled(canExport());
        exportButton.addActionListener(event -> exportData());
        pdfButton.setEnabled(canExport());
        pdfButton.addActionListener(event -> exportPdf());
        importButton.setEnabled(canImport());
        importButton.addActionListener(event -> importData());
        actions.add(refreshButton);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(exportButton);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(pdfButton);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(importButton);

        wrapper.add(actions);
        return wrapper;
    }

    private JPanel buildContentSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusPanel.add(statusLabel);

        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(buildTabs(), BorderLayout.CENTER);
        return panel;
    }

    private Component buildTabs() {
        paymentTabs.removeAll();
        paymentTabs.addTab("SPP Level", buildLevelPaymentsTab());
        paymentTabs.addTab("Rate Gaji Coach", buildCoachPaymentsTab());
        paymentTabs.addTab("Pembayaran Murid", buildStudentPaymentsTab());
        paymentTabs.addTab("Pembayaran Gaji Coach", buildCoachSalaryPaymentsTab());
        return paymentTabs;
    }

    private JPanel buildLevelPaymentsTab() {
        JTable table = new JTable(levelPaymentModel);
        table.setRowHeight(34);
        hideUuidColumn(table, 0);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionBadgeRenderer("Edit Nominal", new Color(14, 116, 144)));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int col = table.columnAtPoint(event.getPoint());
                if (row < 0 || col != 4 || !canUpdate()) {
                    return;
                }
                openLevelPaymentDialog(String.valueOf(levelPaymentModel.getValueAt(table.convertRowIndexToModel(row), 0)));
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCoachPaymentsTab() {
        JTable rateTable = new JTable(gradeCoachModel);
        rateTable.setRowHeight(34);
        hideUuidColumn(rateTable, 0);
        rateTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        rateTable.getColumnModel().getColumn(4).setCellRenderer(new ActionBadgeRenderer("Edit Rate", new Color(14, 116, 144)));
        rateTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = rateTable.rowAtPoint(event.getPoint());
                int col = rateTable.columnAtPoint(event.getPoint());
                if (row < 0 || col != 4 || !canUpdate()) {
                    return;
                }
                openGradeCoachRateDialog(String.valueOf(gradeCoachModel.getValueAt(rateTable.convertRowIndexToModel(row), 0)));
            }
        });

        JTable summaryTable = new JTable(coachSummaryModel);
        summaryTable.setRowHeight(32);

        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);
        wrapper.add(new JScrollPane(rateTable), BorderLayout.NORTH);
        wrapper.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildStudentPaymentsTab() {
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        yearFilterCombo.setPreferredSize(new Dimension(100, 34));
        monthFilterCombo.setPreferredSize(new Dimension(120, 34));
        levelFilterCombo.setPreferredSize(new Dimension(180, 34));
        statusFilterCombo.setPreferredSize(new Dimension(150, 34));
        RoundedButton filterButton = createActionButton("\u2315 Filter", new Color(30, 64, 175), 120);
        RoundedButton clearButton = createActionButton("\u2715 Clear", new Color(100, 116, 139), 120);
        filterButton.addActionListener(event -> loadStudentPayments());
        clearButton.addActionListener(event -> {
            initPeriodFilters();
            initLevelFilter();
            statusFilterCombo.setSelectedIndex(0);
            loadStudentPayments();
        });

        filters.add(new JLabel("Tahun"));
        filters.add(yearFilterCombo);
        filters.add(new JLabel("Bulan"));
        filters.add(monthFilterCombo);
        filters.add(new JLabel("Level"));
        filters.add(levelFilterCombo);
        filters.add(new JLabel("Status"));
        filters.add(statusFilterCombo);
        filters.add(filterButton);
        filters.add(clearButton);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentPaymentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCoachSalaryPaymentsTab() {
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);
        coachYearFilterCombo.setPreferredSize(new Dimension(100, 34));
        coachMonthFilterCombo.setPreferredSize(new Dimension(120, 34));
        coachGradeFilterCombo.setPreferredSize(new Dimension(180, 34));
        coachStatusFilterCombo.setPreferredSize(new Dimension(150, 34));
        RoundedButton filterButton = createActionButton("\u2315 Filter", new Color(30, 64, 175), 120);
        RoundedButton clearButton = createActionButton("\u2715 Clear", new Color(100, 116, 139), 120);
        filterButton.addActionListener(event -> loadCoachSalaryPayments());
        clearButton.addActionListener(event -> {
            initPeriodFilters();
            initCoachGradeFilter();
            coachStatusFilterCombo.setSelectedIndex(0);
            loadCoachSalaryPayments();
        });

        filters.add(new JLabel("Tahun"));
        filters.add(coachYearFilterCombo);
        filters.add(new JLabel("Bulan"));
        filters.add(coachMonthFilterCombo);
        filters.add(new JLabel("Grade"));
        filters.add(coachGradeFilterCombo);
        filters.add(new JLabel("Status"));
        filters.add(coachStatusFilterCombo);
        filters.add(filterButton);
        filters.add(clearButton);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(coachSalaryPaymentTable), BorderLayout.CENTER);
        return panel;
    }

    private void configureStudentPaymentTable() {
        studentPaymentTable.setRowHeight(34);
        studentPaymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideUuidColumn(studentPaymentTable, 0);
        studentPaymentTable.getColumnModel().getColumn(9).setPreferredWidth(150);
        studentPaymentTable.getColumnModel().getColumn(9).setCellRenderer(new ActionBadgeRenderer("Catatan", new Color(22, 163, 74)));
        studentPaymentTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new javax.swing.JCheckBox()));
        studentPaymentModel.addTableModelListener(event -> {
            if (suppressPaymentEvents || event.getColumn() != 6 || event.getFirstRow() < 0) {
                return;
            }
            int row = event.getFirstRow();
            StudentPaymentRecord record = row < paymentRows.size() ? paymentRows.get(row) : null;
            if (record == null) {
                return;
            }
            boolean paid = Boolean.TRUE.equals(studentPaymentModel.getValueAt(row, 6));
            try {
                paymentManagementService.updateStudentPaymentStatus(record.getUuid(), paid, record.getNotes());
                loadStudentPayments();
                setStatus("Status pembayaran murid berhasil diperbarui.", false);
            } catch (Exception exception) {
                setStatus(exception.getMessage(), true);
                loadStudentPayments();
            }
        });

        studentPaymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = studentPaymentTable.rowAtPoint(event.getPoint());
                int col = studentPaymentTable.columnAtPoint(event.getPoint());
                if (row < 0 || col != 9 || !canUpdate()) {
                    return;
                }
                int modelRow = studentPaymentTable.convertRowIndexToModel(row);
                if (modelRow >= 0 && modelRow < paymentRows.size()) {
                    openPaymentNoteDialog(paymentRows.get(modelRow));
                }
            }
        });
    }

    private void configureCoachSalaryPaymentTable() {
        coachSalaryPaymentTable.setRowHeight(34);
        coachSalaryPaymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideUuidColumn(coachSalaryPaymentTable, 0);
        coachSalaryPaymentTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        coachSalaryPaymentTable.getColumnModel().getColumn(7).setCellRenderer(new ActionBadgeRenderer("Catatan", new Color(249, 115, 22)));
        coachSalaryPaymentTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new javax.swing.JCheckBox()));
        coachSalaryPaymentModel.addTableModelListener(event -> {
            if (suppressCoachPaymentEvents || event.getColumn() != 4 || event.getFirstRow() < 0) {
                return;
            }
            int row = event.getFirstRow();
            CoachSalaryPaymentRecord record = row < coachPaymentRows.size() ? coachPaymentRows.get(row) : null;
            if (record == null) {
                return;
            }
            boolean paid = Boolean.TRUE.equals(coachSalaryPaymentModel.getValueAt(row, 4));
            try {
                paymentManagementService.updateCoachSalaryPaymentStatus(record.getUuid(), paid, record.getNotes());
                loadCoachSalaryPayments();
                setStatus("Status pembayaran gaji coach berhasil diperbarui.", false);
            } catch (Exception exception) {
                setStatus(exception.getMessage(), true);
                loadCoachSalaryPayments();
            }
        });

        coachSalaryPaymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = coachSalaryPaymentTable.rowAtPoint(event.getPoint());
                int col = coachSalaryPaymentTable.columnAtPoint(event.getPoint());
                if (row < 0 || col != 7 || !canUpdate()) {
                    return;
                }
                int modelRow = coachSalaryPaymentTable.convertRowIndexToModel(row);
                if (modelRow >= 0 && modelRow < coachPaymentRows.size()) {
                    openCoachPaymentNoteDialog(coachPaymentRows.get(modelRow));
                }
            }
        });
    }

    private void reloadAll() {
        loadLevelPayments();
        loadGradeCoachPayments();
        loadCoachSummaries();
        loadStudentPayments();
        loadCoachSalaryPayments();
    }

    private void loadLevelPayments() {
        levelPaymentModel.setRowCount(0);
        for (LevelPaymentConfig config : paymentManagementService.getLevelPaymentConfigs()) {
            levelPaymentModel.addRow(new Object[]{
                    config.getLevelUuid(),
                    config.getLevelName(),
                    config.getGradeName(),
                    formatCurrency(config.getMonthlySpp()),
                    ""
            });
        }
    }

    private void loadGradeCoachPayments() {
        gradeCoachModel.setRowCount(0);
        for (GradeCoachPaymentRate rate : paymentManagementService.getGradeCoachPaymentRates()) {
            gradeCoachModel.addRow(new Object[]{
                    rate.getGradeUuid(),
                    rate.getGradeName(),
                    formatCurrency(rate.getMonthlyRate()),
                    String.valueOf(rate.getCoachCount()),
                    ""
            });
        }
    }

    private void loadCoachSummaries() {
        coachSummaryModel.setRowCount(0);
        for (CoachPaymentSummary row : paymentManagementService.getCoachPaymentSummaries()) {
            coachSummaryModel.addRow(new Object[]{
                    row.getCoachName(),
                    row.getGradeName() == null || row.getGradeName().trim().isEmpty() ? "-" : row.getGradeName(),
                    formatCurrency(row.getMonthlyRate())
            });
        }
    }

    private void loadStudentPayments() {
        Integer selectedYear = (Integer) yearFilterCombo.getSelectedItem();
        Integer selectedMonth = (Integer) monthFilterCombo.getSelectedItem();
        LevelOption levelOption = (LevelOption) levelFilterCombo.getSelectedItem();
        if (selectedYear == null || selectedMonth == null) {
            return;
        }
        List<StudentPaymentRecord> rows = paymentManagementService.getStudentPayments(
                selectedYear,
                selectedMonth,
                levelOption == null ? "" : levelOption.uuid,
                String.valueOf(statusFilterCombo.getSelectedItem()));
        paymentRows.clear();
        paymentRows.addAll(rows);
        suppressPaymentEvents = true;
        studentPaymentModel.setRowCount(0);
        for (StudentPaymentRecord row : rows) {
            studentPaymentModel.addRow(new Object[]{
                    row.getUuid(),
                    row.getMuridName(),
                    row.getUsername(),
                    row.getGradeName(),
                    row.getLevelName(),
                    formatCurrency(row.getSppAmount()),
                    row.isPaid(),
                    formatDateTime(row.getPaidAt()),
                    row.getNotes() == null || row.getNotes().trim().isEmpty() ? "-" : row.getNotes(),
                    ""
            });
        }
        suppressPaymentEvents = false;
    }

    private void loadCoachSalaryPayments() {
        Integer selectedYear = (Integer) coachYearFilterCombo.getSelectedItem();
        Integer selectedMonth = (Integer) coachMonthFilterCombo.getSelectedItem();
        if (selectedYear == null || selectedMonth == null) {
            return;
        }
        List<CoachSalaryPaymentRecord> rows = paymentManagementService.getCoachSalaryPayments(
                selectedYear,
                selectedMonth,
                normalizeFilterValue(String.valueOf(coachGradeFilterCombo.getSelectedItem()), "Semua Grade"),
                String.valueOf(coachStatusFilterCombo.getSelectedItem()));
        coachPaymentRows.clear();
        coachPaymentRows.addAll(rows);
        suppressCoachPaymentEvents = true;
        coachSalaryPaymentModel.setRowCount(0);
        for (CoachSalaryPaymentRecord row : rows) {
            coachSalaryPaymentModel.addRow(new Object[]{
                    row.getUuid(),
                    row.getCoachName(),
                    row.getGradeName(),
                    formatCurrency(row.getSalaryAmount()),
                    row.isPaid(),
                    formatDateTime(row.getPaidAt()),
                    row.getNotes() == null || row.getNotes().trim().isEmpty() ? "-" : row.getNotes(),
                    ""
            });
        }
        suppressCoachPaymentEvents = false;
    }

    private void openLevelPaymentDialog(String levelUuid) {
        LevelPaymentConfig selected = null;
        for (LevelPaymentConfig config : paymentManagementService.getLevelPaymentConfigs()) {
            if (config.getLevelUuid().equals(levelUuid)) {
                selected = config;
                break;
            }
        }
        if (selected == null) {
            return;
        }
        final LevelPaymentConfig selectedConfig = selected;
        JTextField amountField = new JTextField(selectedConfig.getMonthlySpp() == null ? "0" : selectedConfig.getMonthlySpp().stripTrailingZeros().toPlainString());
        showAmountDialog(
                "SPP Level",
                "SPP bulanan untuk level " + selectedConfig.getLevelName(),
                amountField,
                () -> paymentManagementService.saveLevelPaymentConfig(selectedConfig.getLevelUuid(), amountField.getText()),
                "Konfigurasi SPP level berhasil disimpan.");
    }

    private void openGradeCoachRateDialog(String gradeUuid) {
        GradeCoachPaymentRate selected = null;
        for (GradeCoachPaymentRate config : paymentManagementService.getGradeCoachPaymentRates()) {
            if (config.getGradeUuid().equals(gradeUuid)) {
                selected = config;
                break;
            }
        }
        if (selected == null) {
            return;
        }
        final GradeCoachPaymentRate selectedRate = selected;
        JTextField amountField = new JTextField(selectedRate.getMonthlyRate() == null ? "0" : selectedRate.getMonthlyRate().stripTrailingZeros().toPlainString());
        showAmountDialog(
                "Rate Coach per Grade",
                "Pembayaran coach untuk grade " + selectedRate.getGradeName(),
                amountField,
                () -> paymentManagementService.saveGradeCoachPaymentRate(selectedRate.getGradeUuid(), amountField.getText()),
                "Rate pembayaran coach berhasil disimpan.");
    }

    private void openPaymentNoteDialog(StudentPaymentRecord record) {
        JDialog dialog = new JDialog((Frame) null, "Catatan Pembayaran Murid", true);
        dialog.setLayout(new BorderLayout(0, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        JTextArea notesArea = new JTextArea(record.getNotes() == null ? "" : record.getNotes(), 5, 28);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dialog.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(Color.WHITE);
        RoundedButton saveButton = createActionButton("Simpan", new Color(14, 116, 144), 110);
        RoundedButton cancelButton = createActionButton("Batal", new Color(100, 116, 139), 110);
        saveButton.addActionListener(event -> {
            try {
                paymentManagementService.updateStudentPaymentStatus(record.getUuid(), record.isPaid(), notesArea.getText());
                dialog.dispose();
                loadStudentPayments();
                setStatus("Catatan pembayaran murid berhasil diperbarui.", false);
            } catch (Exception exception) {
                setStatus(exception.getMessage(), true);
            }
        });
        cancelButton.addActionListener(event -> dialog.dispose());
        footer.add(cancelButton);
        footer.add(saveButton);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void openCoachPaymentNoteDialog(CoachSalaryPaymentRecord record) {
        JDialog dialog = new JDialog((Frame) null, "Catatan Pembayaran Gaji Coach", true);
        dialog.setLayout(new BorderLayout(0, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        JTextArea notesArea = new JTextArea(record.getNotes() == null ? "" : record.getNotes(), 5, 28);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dialog.add(new JScrollPane(notesArea), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(Color.WHITE);
        RoundedButton saveButton = createActionButton("Simpan", new Color(14, 116, 144), 110);
        RoundedButton cancelButton = createActionButton("Batal", new Color(100, 116, 139), 110);
        saveButton.addActionListener(event -> {
            try {
                paymentManagementService.updateCoachSalaryPaymentStatus(record.getUuid(), record.isPaid(), notesArea.getText());
                dialog.dispose();
                loadCoachSalaryPayments();
                setStatus("Catatan pembayaran gaji coach berhasil diperbarui.", false);
            } catch (Exception exception) {
                setStatus(exception.getMessage(), true);
            }
        });
        cancelButton.addActionListener(event -> dialog.dispose());
        footer.add(cancelButton);
        footer.add(saveButton);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAmountDialog(String title, String helperText, JTextField amountField, Runnable saveAction, String successMessage) {
        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setLayout(new BorderLayout(0, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 0, 16));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        JLabel helperLabel = new JLabel(helperText);
        helperLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        helperLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(helperLabel);
        body.add(Box.createVerticalStrut(10));
        body.add(amountField);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        footer.setBackground(Color.WHITE);
        RoundedButton saveButton = createActionButton("Simpan", new Color(14, 116, 144), 110);
        RoundedButton cancelButton = createActionButton("Batal", new Color(100, 116, 139), 110);
        saveButton.addActionListener(event -> {
            try {
                saveAction.run();
                dialog.dispose();
                reloadAll();
                setStatus(successMessage, false);
            } catch (Exception exception) {
                setStatus(exception.getMessage(), true);
            }
        });
        cancelButton.addActionListener(event -> dialog.dispose());
        footer.add(cancelButton);
        footer.add(saveButton);

        dialog.add(body, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void exportData() {
        if (!canExport()) {
            setStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Pembayaran (Excel)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = normalizeChosenFile(chooser, true);
        if (file == null) {
            setStatus("Lokasi file export tidak valid.", true);
            return;
        }
        try {
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = appendExtension(file, "xlsx");
            }
            exportCurrentTabXlsx(file);
            setStatus("Export Excel berhasil untuk tab " + paymentTabs.getTitleAt(paymentTabs.getSelectedIndex()) + ".", false);
        } catch (Exception exception) {
            setStatus("Gagal export Excel: " + exception.getMessage(), true);
        }
    }

    private void exportPdf() {
        if (!canExport()) {
            setStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Pembayaran (PDF)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file == null) {
            setStatus("Lokasi file export tidak valid.", true);
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = appendExtension(file, "pdf");
        }
        try {
            exportCurrentTabPdf(file);
            setStatus("Export PDF berhasil untuk tab " + paymentTabs.getTitleAt(paymentTabs.getSelectedIndex()) + ".", false);
        } catch (NoClassDefFoundError error) {
            setStatus("Library Jasper belum lengkap: " + error.getMessage(), true);
        } catch (Exception exception) {
            setStatus("Gagal export PDF: " + rootErrorMessage(exception), true);
        }
    }

    private void importData() {
        if (!canImport()) {
            setStatus("Anda tidak punya izin import.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Master Pembayaran");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = normalizeChosenFile(chooser, false);
        if (file == null || !file.exists()) {
            setStatus("File import tidak ditemukan.", true);
            return;
        }
        try {
            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                importCurrentTabXlsx(file);
            } else if (file.getName().toLowerCase().endsWith(".csv")) {
                importCurrentTabCsv(file);
            } else {
                throw new IllegalArgumentException("Format file tidak didukung. Gunakan CSV atau XLSX.");
            }
            reloadAll();
        } catch (Exception exception) {
            setStatus("Gagal import: " + exception.getMessage(), true);
        }
    }

    private void exportCurrentTabXlsx(File file) throws Exception {
        writeXlsx(file, sanitizeSheetName(paymentTabs.getTitleAt(paymentTabs.getSelectedIndex())), getCurrentTabRows());
    }

    private void exportCurrentTabPdf(File file) throws JRException {
        List<String[]> rows = getCurrentTabRows();
        String[] headers = rows.isEmpty() ? new String[0] : rows.get(0);
        List<GenericExportRow> exportRows = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            exportRows.add(new GenericExportRow(rows.get(i)));
        }

        String jrxml = buildGenericJasperTemplate(paymentTabs.getTitleAt(paymentTabs.getSelectedIndex()), headers);
        InputStream templateStream = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), new JRBeanCollectionDataSource(exportRows));
        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
    }

    private List<String[]> getCurrentTabRows() {
        int tabIndex = paymentTabs.getSelectedIndex();
        List<String[]> rows = new ArrayList<>();
        if (tabIndex == 0) {
            rows.add(new String[]{"level", "grade", "spp_bulanan"});
            for (LevelPaymentConfig config : paymentManagementService.getLevelPaymentConfigs()) {
                rows.add(new String[]{safe(config.getLevelName()), safe(config.getGradeName()), plainAmount(config.getMonthlySpp())});
            }
            return rows;
        }
        if (tabIndex == 1) {
            rows.add(new String[]{"grade", "rate_gaji_coach", "jumlah_coach"});
            for (GradeCoachPaymentRate rate : paymentManagementService.getGradeCoachPaymentRates()) {
                rows.add(new String[]{safe(rate.getGradeName()), plainAmount(rate.getMonthlyRate()), String.valueOf(rate.getCoachCount())});
            }
            return rows;
        }
        if (tabIndex == 2) {
            rows.add(new String[]{"murid", "username", "grade", "level", "spp", "paid", "paid_at", "catatan"});
            for (StudentPaymentRecord row : paymentRows) {
                rows.add(new String[]{
                        safe(row.getMuridName()),
                        safe(row.getUsername()),
                        safe(row.getGradeName()),
                        safe(row.getLevelName()),
                        plainAmount(row.getSppAmount()),
                        row.isPaid() ? "Ya" : "Tidak",
                        formatDateTime(row.getPaidAt()),
                        safe(row.getNotes())
                });
            }
            return rows;
        }
        rows.add(new String[]{"coach", "grade", "nominal", "paid", "paid_at", "catatan"});
        for (CoachSalaryPaymentRecord row : coachPaymentRows) {
            rows.add(new String[]{
                    safe(row.getCoachName()),
                    safe(row.getGradeName()),
                    plainAmount(row.getSalaryAmount()),
                    row.isPaid() ? "Ya" : "Tidak",
                    formatDateTime(row.getPaidAt()),
                    safe(row.getNotes())
            });
        }
        return rows;
    }

    private void importCurrentTabCsv(File file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                rows.add(parseCsvLine(line));
            }
        }
        processImportedRows(rows);
    }

    private void importCurrentTabXlsx(File file) throws Exception {
        processImportedRows(readSheetRows(file));
    }

    private void processImportedRows(List<String[]> rows) {
        int success = 0;
        int failed = 0;
        int tabIndex = paymentTabs.getSelectedIndex();
        for (int i = 0; i < rows.size(); i++) {
            String[] parts = rows.get(i);
            if (parts.length == 0) {
                continue;
            }
            if (i == 0) {
                continue;
            }
            try {
                if (tabIndex == 0) {
                    if (parts.length < 3) {
                        throw new IllegalArgumentException("Kolom kurang.");
                    }
                    paymentManagementService.saveLevelPaymentConfig(resolveLevelUuid(parts[0]), parts[2]);
                } else if (tabIndex == 1) {
                    if (parts.length < 2) {
                        throw new IllegalArgumentException("Kolom kurang.");
                    }
                    paymentManagementService.saveGradeCoachPaymentRate(resolveGradeUuid(parts[0]), parts[1]);
                } else if (tabIndex == 2) {
                    if (parts.length < 6) {
                        throw new IllegalArgumentException("Kolom kurang.");
                    }
                    paymentManagementService.updateStudentPaymentStatus(
                            findStudentPaymentUuid(parts[1], parts[0]),
                            parsePaid(parts[5]),
                            parts.length > 7 ? parts[7] : "");
                } else {
                    if (parts.length < 4) {
                        throw new IllegalArgumentException("Kolom kurang.");
                    }
                    paymentManagementService.updateCoachSalaryPaymentStatus(
                            findCoachPaymentUuid(parts[0], parts[1]),
                            parsePaid(parts[3]),
                            parts.length > 5 ? parts[5] : "");
                }
                success++;
            } catch (Exception exception) {
                failed++;
            }
        }
        setStatus("Import tab " + paymentTabs.getTitleAt(tabIndex) + " selesai. Berhasil: " + success + ", gagal: " + failed, failed > 0);
    }

    private String findStudentPaymentUuid(String username, String muridName) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedName = muridName == null ? "" : muridName.trim();
        for (StudentPaymentRecord row : paymentRows) {
            if (normalizedUsername.equalsIgnoreCase(safe(row.getUsername()))
                    || normalizedName.equalsIgnoreCase(safe(row.getMuridName()))) {
                return row.getUuid();
            }
        }
        throw new IllegalArgumentException("Pembayaran murid tidak ditemukan.");
    }

    private String findCoachPaymentUuid(String coachName, String gradeName) {
        String normalizedCoach = coachName == null ? "" : coachName.trim();
        String normalizedGrade = gradeName == null ? "" : gradeName.trim();
        for (CoachSalaryPaymentRecord row : coachPaymentRows) {
            if (normalizedCoach.equalsIgnoreCase(safe(row.getCoachName()))
                    && normalizedGrade.equalsIgnoreCase(safe(row.getGradeName()))) {
                return row.getUuid();
            }
        }
        throw new IllegalArgumentException("Pembayaran coach tidak ditemukan.");
    }

    private String resolveLevelUuid(String levelName) {
        String normalized = levelName == null ? "" : levelName.trim();
        for (Level level : paymentManagementService.getLevels()) {
            if (level != null && normalized.equalsIgnoreCase(level.getName())) {
                return level.getUuid();
            }
        }
        throw new IllegalArgumentException("Level tidak ditemukan: " + normalized);
    }

    private String resolveGradeUuid(String gradeName) {
        String normalized = gradeName == null ? "" : gradeName.trim();
        for (GradeCoachPaymentRate rate : paymentManagementService.getGradeCoachPaymentRates()) {
            if (rate != null && normalized.equalsIgnoreCase(rate.getGradeName())) {
                return rate.getGradeUuid();
            }
        }
        throw new IllegalArgumentException("Grade tidak ditemukan: " + normalized);
    }

    private boolean parsePaid(String rawValue) {
        String normalized = rawValue == null ? "" : rawValue.trim();
        return "ya".equalsIgnoreCase(normalized) || "true".equalsIgnoreCase(normalized) || "1".equals(normalized)
                || "paid".equalsIgnoreCase(normalized) || "sudah bayar".equalsIgnoreCase(normalized);
    }

    private String buildGenericJasperTemplate(String title, String[] headers) {
        int columnCount = headers.length;
        int pageWidth = 842;
        int usableWidth = 802;
        int[] widths = distributeWidths(usableWidth, Math.max(1, columnCount));
        StringBuilder template = new StringBuilder();
        template.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports ")
                .append("http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" ")
                .append("name=\"master_payment_report\" pageWidth=\"").append(pageWidth).append("\" pageHeight=\"595\" orientation=\"Landscape\" ")
                .append("columnWidth=\"").append(usableWidth).append("\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">");

        for (int i = 0; i < columnCount; i++) {
            template.append("<field name=\"c").append(i + 1).append("\" class=\"java.lang.String\"/>");
        }

        template.append("<title><band height=\"36\">")
                .append("<staticText><reportElement x=\"0\" y=\"0\" width=\"802\" height=\"28\"/>")
                .append("<textElement><font size=\"14\" isBold=\"true\"/></textElement>")
                .append("<text><![CDATA[").append(escapeXml(title)).append(" Export]]></text></staticText>")
                .append("</band></title>");

        template.append("<columnHeader><band height=\"22\">");
        int x = 0;
        for (int i = 0; i < columnCount; i++) {
            template.append(buildHeaderText(x, widths[i], headers[i]));
            x += widths[i];
        }
        template.append("</band></columnHeader>");

        template.append("<detail><band height=\"20\">");
        x = 0;
        for (int i = 0; i < columnCount; i++) {
            template.append(buildDetailTextField(x, widths[i], "c" + (i + 1)));
            x += widths[i];
        }
        template.append("</band></detail></jasperReport>");
        return template.toString();
    }

    private int[] distributeWidths(int totalWidth, int columnCount) {
        int[] widths = new int[columnCount];
        int base = totalWidth / columnCount;
        int remainder = totalWidth % columnCount;
        for (int i = 0; i < columnCount; i++) {
            widths[i] = base + (i < remainder ? 1 : 0);
        }
        return widths;
    }

    private String buildHeaderText(int x, int width, String text) {
        return "<staticText><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"20\"/>"
                + "<box><pen lineWidth=\"0.6\" lineColor=\"#CBD5E1\"/></box>"
                + "<textElement><font size=\"10\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[" + escapeXml(text) + "]]></text></staticText>";
    }

    private String buildDetailTextField(int x, int width, String field) {
        return "<textField textAdjust=\"StretchHeight\"><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"18\"/>"
                + "<box><pen lineWidth=\"0.5\" lineColor=\"#CBD5E1\"/></box>"
                + "<textElement><font size=\"9\"/></textElement>"
                + "<textFieldExpression><![CDATA[$F{" + field + "}]]></textFieldExpression></textField>";
    }

    private void writeXlsx(File file, String sheetName, List<String[]> rows) throws Exception {
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
                            + "<sheets><sheet name=\"" + escapeXml(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                            + "</Relationships>");
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", buildSheetXml(rows));
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
                    int col = columnIndex(cell.getAttribute("r"));
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
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
                continue;
            }
            if (ch == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String rootErrorMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.trim().isEmpty() ? current.getClass().getSimpleName() : message;
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
        if (saveMode && !resolved.getName().toLowerCase().endsWith(".xlsx") && !resolved.getName().toLowerCase().endsWith(".csv")) {
            resolved = appendExtension(resolved, "xlsx");
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

    private String sanitizeSheetName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Sheet1";
        }
        return value.replaceAll("[\\\\/:*?\\[\\]]", "").replace(" ", "");
    }

    private String plainAmount(BigDecimal amount) {
        return amount == null ? "0" : amount.stripTrailingZeros().toPlainString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void initPeriodFilters() {
        yearFilterCombo.removeAllItems();
        coachYearFilterCombo.removeAllItems();
        int currentYear = LocalDateTime.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            yearFilterCombo.addItem(year);
            coachYearFilterCombo.addItem(year);
        }
        yearFilterCombo.setSelectedItem(currentYear);
        coachYearFilterCombo.setSelectedItem(currentYear);

        monthFilterCombo.removeAllItems();
        coachMonthFilterCombo.removeAllItems();
        for (int month = 1; month <= 12; month++) {
            monthFilterCombo.addItem(month);
            coachMonthFilterCombo.addItem(month);
        }
        monthFilterCombo.setSelectedItem(LocalDateTime.now().getMonthValue());
        coachMonthFilterCombo.setSelectedItem(LocalDateTime.now().getMonthValue());
    }

    private void initLevelFilter() {
        levelFilterCombo.removeAllItems();
        levelFilterCombo.addItem(new LevelOption("", "Semua Level"));
        for (Level level : paymentManagementService.getLevels()) {
            levelFilterCombo.addItem(new LevelOption(level.getUuid(), level.getName()));
        }
    }

    private void initCoachGradeFilter() {
        coachGradeFilterCombo.removeAllItems();
        coachGradeFilterCombo.addItem("Semua Grade");
        for (GradeCoachPaymentRate rate : paymentManagementService.getGradeCoachPaymentRates()) {
            if (rate.getGradeName() != null && !rate.getGradeName().trim().isEmpty()) {
                coachGradeFilterCombo.addItem(rate.getGradeName());
            }
        }
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void hideUuidColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(columnIndex).setWidth(0);
    }

    private String formatCurrency(BigDecimal amount) {
        return IDR_FORMAT.format(amount == null ? BigDecimal.ZERO : amount);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String normalizeFilterValue(String rawValue, String allLabel) {
        if (rawValue == null || rawValue.trim().isEmpty() || allLabel.equalsIgnoreCase(rawValue.trim())) {
            return "";
        }
        return rawValue.trim();
    }

    private void setStatus(String message, boolean error) {
        statusLabel.setText(message == null || message.trim().isEmpty() ? " " : message);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(error ? new Color(220, 38, 38) : new Color(22, 163, 74));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(error ? new Color(254, 226, 226) : new Color(220, 252, 231));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private boolean canUpdate() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canUpdate());
    }

    private boolean canExport() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canExport());
    }

    private boolean canImport() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canImport());
    }

    private static final class ActionBadgeRenderer extends JLabel implements TableCellRenderer {
        private ActionBadgeRenderer(String text, Color background) {
            setText(text);
            setOpaque(true);
            setBackground(background);
            setForeground(Color.WHITE);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private static final class LevelOption {
        private final String uuid;
        private final String name;

        private LevelOption(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final class GenericExportRow {
        private final String[] columns;

        public GenericExportRow(String[] columns) {
            this.columns = columns == null ? new String[0] : columns;
        }

        public String getC1() { return get(0); }
        public String getC2() { return get(1); }
        public String getC3() { return get(2); }
        public String getC4() { return get(3); }
        public String getC5() { return get(4); }
        public String getC6() { return get(5); }
        public String getC7() { return get(6); }
        public String getC8() { return get(7); }
        public String getC9() { return get(8); }

        private String get(int index) {
            return index < columns.length && columns[index] != null ? columns[index] : "";
        }
    }
}
