package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.CoachPaymentSummary;
import com.tugasbesar.app.model.GradeCoachPaymentRate;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.LevelPaymentConfig;
import com.tugasbesar.app.model.StudentPaymentRecord;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.PaymentManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MasterPaymentScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final NumberFormat IDR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    private final User currentUser;
    private final AppModule modulePermission;
    private final Runnable sessionRefreshAction;
    private final PaymentManagementService paymentManagementService;

    private final JLabel statusLabel;
    private final DefaultTableModel levelPaymentModel;
    private final DefaultTableModel gradeCoachModel;
    private final DefaultTableModel coachSummaryModel;
    private final DefaultTableModel studentPaymentModel;
    private final JTable studentPaymentTable;
    private final JComboBox<Integer> yearFilterCombo;
    private final JComboBox<Integer> monthFilterCombo;
    private final JComboBox<LevelOption> levelFilterCombo;
    private final JComboBox<String> statusFilterCombo;
    private final List<StudentPaymentRecord> paymentRows;
    private boolean suppressPaymentEvents;

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
        this.yearFilterCombo = new JComboBox<>();
        this.monthFilterCombo = new JComboBox<>();
        this.levelFilterCombo = new JComboBox<>();
        this.statusFilterCombo = new JComboBox<>(new String[]{"Semua Status", "Sudah Bayar", "Belum Bayar"});
        this.paymentRows = new ArrayList<>();

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        initPeriodFilters();
        initLevelFilter();
        configureStudentPaymentTable();
        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);
        reloadAll();
    }

    private JPanel buildToolbar() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        RoundedButton refreshButton = createActionButton("\u21BB Refresh", new Color(71, 85, 105), 150);
        refreshButton.addActionListener(event -> reloadAll());
        actions.add(refreshButton);

        wrapper.add(actions);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(statusLabel);
        return wrapper;
    }

    private Component buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("SPP Level", buildLevelPaymentsTab());
        tabs.addTab("Coach by Grade", buildCoachPaymentsTab());
        tabs.addTab("Pembayaran Murid", buildStudentPaymentsTab());
        return tabs;
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

    private void reloadAll() {
        loadLevelPayments();
        loadGradeCoachPayments();
        loadCoachSummaries();
        loadStudentPayments();
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

    private void initPeriodFilters() {
        yearFilterCombo.removeAllItems();
        int currentYear = LocalDateTime.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 1; year++) {
            yearFilterCombo.addItem(year);
        }
        yearFilterCombo.setSelectedItem(currentYear);

        monthFilterCombo.removeAllItems();
        for (int month = 1; month <= 12; month++) {
            monthFilterCombo.addItem(month);
        }
        monthFilterCombo.setSelectedItem(LocalDateTime.now().getMonthValue());
    }

    private void initLevelFilter() {
        levelFilterCombo.removeAllItems();
        levelFilterCombo.addItem(new LevelOption("", "Semua Level"));
        for (Level level : paymentManagementService.getLevels()) {
            levelFilterCombo.addItem(new LevelOption(level.getUuid(), level.getName()));
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
}
