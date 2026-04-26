package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.AttendanceChecklistItem;
import com.tugasbesar.app.model.AttendanceForm;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AttendanceFormManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MasterAttendanceScreen extends JPanel {
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String SEARCH_ICON = "\u2315";
    private static final String CLEAR_ICON = "\u2715";
    private static final String VIEW_ICON = "\uD83D\uDD0D";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";

    private final User currentUser;
    private final AppModule modulePermission;
    private final AttendanceFormManagementService attendanceFormManagementService;
    private final List<AttendanceForm> sourceItems;
    private final List<User> coachUsers;
    private final List<Level> levels;

    private final DefaultTableModel tableModel;
    private final JTable formTable;
    private final JLabel statusLabel;
    private final JLabel dataInfoLabel;

    private final JComboBox<UserOption> coachFilterCombo;
    private final JComboBox<LevelOption> levelFilterCombo;
    private final JComboBox<String> pertemuanFilterCombo;
    private final JSpinner dateFilterSpinner;
    private final JCheckBox useDateFilterCheck;

    public MasterAttendanceScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.attendanceFormManagementService = new AttendanceFormManagementService();
        this.sourceItems = new ArrayList<>();
        this.coachUsers = attendanceFormManagementService.getCoachUsers();
        this.levels = attendanceFormManagementService.getLevels();
        this.tableModel = new DefaultTableModel(new String[]{"UUID", "Coach", "Tanggal", "Pertemuan", "Class", "Status", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.formTable = new JTable(tableModel);
        this.statusLabel = new JLabel(" ");
        this.dataInfoLabel = new JLabel("Data ditampilkan: 0");
        this.coachFilterCombo = new JComboBox<>();
        this.levelFilterCombo = new JComboBox<>();
        this.pertemuanFilterCombo = new JComboBox<>(new String[]{"Semua Pertemuan", "1", "2", "3", "4", "5", "6", "7", "8"});
        this.dateFilterSpinner = createDateSpinner();
        this.useDateFilterCheck = new JCheckBox("Pakai Date");

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        initFilters();
        add(buildTopSection(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildTopSection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setOpaque(false);
        RoundedButton addButton = createActionButton(ADD_ICON + " Buat Form", new Color(14, 116, 144), 154);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 142);
        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> openFormDialog(null));
        refreshButton.addActionListener(event -> loadData());
        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        RoundedButton searchButton = createActionButton(SEARCH_ICON + " Filter", new Color(30, 64, 175), 120);
        RoundedButton clearButton = createActionButton(CLEAR_ICON + " Clear", new Color(100, 116, 139), 120);
        searchButton.addActionListener(event -> loadData());
        clearButton.addActionListener(event -> {
            resetFilters();
            loadData();
        });

        filterPanel.add(new JLabel("Coach"));
        filterPanel.add(coachFilterCombo);
        filterPanel.add(useDateFilterCheck);
        filterPanel.add(dateFilterSpinner);
        filterPanel.add(new JLabel("Class"));
        filterPanel.add(levelFilterCombo);
        filterPanel.add(new JLabel("Pertemuan"));
        filterPanel.add(pertemuanFilterCombo);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        wrapper.add(actionPanel);
        wrapper.add(Box.createVerticalStrut(12));
        wrapper.add(filterPanel);
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

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusPanel.add(statusLabel);
        panel.add(statusPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(formTable);
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

    private void configureTable() {
        formTable.setRowHeight(34);
        formTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        formTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        formTable.setGridColor(new Color(226, 232, 240));
        formTable.setShowVerticalLines(false);
        formTable.getColumnModel().getColumn(0).setMinWidth(0);
        formTable.getColumnModel().getColumn(0).setMaxWidth(0);
        formTable.getColumnModel().getColumn(0).setWidth(0);
        formTable.getColumnModel().getColumn(6).setPreferredWidth(250);
        formTable.getColumnModel().getColumn(6).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(VIEW_ICON + " Cek", new Color(30, 64, 175)));
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144)));
            panel.add(createActionBadge(DELETE_ICON + " Delete", new Color(220, 38, 38)));
            return panel;
        });

        formTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = formTable.rowAtPoint(event.getPoint());
                int viewColumn = formTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 6) {
                    return;
                }
                int modelRow = formTable.convertRowIndexToModel(viewRow);
                AttendanceForm selected = findByUuid(String.valueOf(tableModel.getValueAt(modelRow, 0)));
                if (selected == null) {
                    return;
                }

                int action = resolveActionIndex(viewRow, viewColumn, event.getX(), event.getY());
                if (action == 0) {
                    openChecklistDialog(selected);
                } else if (action == 1) {
                    if (!canUpdate()) {
                        setStatusError("Anda tidak memiliki izin edit master absensi.");
                        return;
                    }
                    openFormDialog(selected);
                } else if (action == 2) {
                    if (!canDelete()) {
                        setStatusError("Anda tidak memiliki izin hapus master absensi.");
                        return;
                    }
                    deleteForm(selected);
                }
            }
        });
    }

    private int resolveActionIndex(int viewRow, int viewColumn, int clickX, int clickY) {
        java.awt.Rectangle rect = formTable.getCellRect(viewRow, viewColumn, false);
        int relativeX = clickX - rect.x;
        int relativeY = clickY - rect.y;

        javax.swing.table.TableCellRenderer renderer = formTable.getCellRenderer(viewRow, viewColumn);
        Component component = formTable.prepareRenderer(renderer, viewRow, viewColumn);
        component.setBounds(0, 0, rect.width, rect.height);
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.doLayout();
            for (int index = 0; index < panel.getComponentCount(); index++) {
                Component child = panel.getComponent(index);
                if (child.getBounds().contains(relativeX, relativeY)) {
                    return index; // 0=cek, 1=edit, 2=delete
                }
            }
        }

        int section = rect.width / 3;
        if (relativeX < section) {
            return 0;
        }
        if (relativeX < section * 2) {
            return 1;
        }
        return 2;
    }

    private JLabel createActionBadge(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setForeground(Color.WHITE);
        label.setBackground(color);
        label.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void initFilters() {
        coachFilterCombo.removeAllItems();
        coachFilterCombo.addItem(new UserOption("", "Semua Coach"));
        for (User coach : coachUsers) {
            coachFilterCombo.addItem(new UserOption(coach.getUuid(), coach.getFullName()));
        }
        coachFilterCombo.setPreferredSize(new Dimension(170, 34));

        levelFilterCombo.removeAllItems();
        levelFilterCombo.addItem(new LevelOption("", "Semua Class"));
        for (Level level : levels) {
            levelFilterCombo.addItem(new LevelOption(level.getUuid(), level.getName()));
        }
        levelFilterCombo.setPreferredSize(new Dimension(150, 34));
        pertemuanFilterCombo.setPreferredSize(new Dimension(140, 34));
        dateFilterSpinner.setPreferredSize(new Dimension(130, 34));
        useDateFilterCheck.setOpaque(false);
        useDateFilterCheck.setSelected(false);
    }

    private void resetFilters() {
        coachFilterCombo.setSelectedIndex(0);
        levelFilterCombo.setSelectedIndex(0);
        pertemuanFilterCombo.setSelectedIndex(0);
        useDateFilterCheck.setSelected(false);
        dateFilterSpinner.setValue(new Date());
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        JFormattedTextField field = editor.getTextField();
        field.setHorizontalAlignment(SwingConstants.LEFT);
        spinner.setValue(new Date());
        return spinner;
    }

    private String selectedDateText() {
        Date date = (Date) dateFilterSpinner.getValue();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private void loadData() {
        try {
            UserOption coachOption = (UserOption) coachFilterCombo.getSelectedItem();
            LevelOption levelOption = (LevelOption) levelFilterCombo.getSelectedItem();
            String pertemuanText = (String) pertemuanFilterCombo.getSelectedItem();
            Integer pertemuan = "Semua Pertemuan".equals(pertemuanText) ? null : Integer.parseInt(pertemuanText);

            sourceItems.clear();
            sourceItems.addAll(attendanceFormManagementService.getFormsByFilters(
                    coachOption == null ? "" : coachOption.uuid,
                    useDateFilterCheck.isSelected() ? selectedDateText() : "",
                    levelOption == null ? "" : levelOption.uuid,
                    pertemuan));

            tableModel.setRowCount(0);
            for (AttendanceForm form : sourceItems) {
                tableModel.addRow(new Object[]{
                        form.getUuid(),
                        safeText(form.getCoachName()),
                        form.getAttendanceDate() == null ? "-" : form.getAttendanceDate().toString(),
                        form.getPertemuanKe(),
                        safeText(form.getLevelName()),
                        form.isActive() ? "Aktif" : "Nonaktif",
                        ""
                });
            }
            dataInfoLabel.setText("Data ditampilkan: " + sourceItems.size());
            setStatusNeutral(" ");
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
        }
    }

    private AttendanceForm findByUuid(String uuid) {
        for (AttendanceForm form : sourceItems) {
            if (form.getUuid().equals(uuid)) {
                return form;
            }
        }
        return null;
    }

    private void openFormDialog(AttendanceForm existing) {
        if (coachUsers.isEmpty()) {
            setStatusError("Belum ada data coach.");
            return;
        }
        if (levels.isEmpty()) {
            setStatusError("Belum ada data level.");
            return;
        }

        JDialog dialog = new JDialog((Frame) null, existing == null ? "Buat Form Absensi" : "Edit Form Absensi", true);
        dialog.setSize(580, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;

        JComboBox<UserOption> coachCombo = new JComboBox<>();
        for (User coach : coachUsers) {
            coachCombo.addItem(new UserOption(coach.getUuid(), coach.getFullName()));
        }

        JComboBox<LevelOption> levelCombo = new JComboBox<>();
        for (Level level : levels) {
            levelCombo.addItem(new LevelOption(level.getUuid(), level.getName()));
        }

        JSpinner dateSpinner = createDateSpinner();
        JComboBox<String> pertemuanCombo = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8"});
        JComboBox<String> activeCombo = new JComboBox<>(new String[]{"Aktif", "Nonaktif"});
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);

        if (existing != null) {
            selectCoach(coachCombo, existing.getCoachUuid());
            selectLevel(levelCombo, existing.getLevelUuid());
            if (existing.getAttendanceDate() != null) {
                dateSpinner.setValue(Date.from(existing.getAttendanceDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
            pertemuanCombo.setSelectedItem(String.valueOf(existing.getPertemuanKe()));
            activeCombo.setSelectedItem(existing.isActive() ? "Aktif" : "Nonaktif");
            notesArea.setText(safeText(existing.getNotes()));
        }

        addFormRow(formPanel, gbc, 0, "Coach", coachCombo);
        addFormRow(formPanel, gbc, 1, "Class (Level)", levelCombo);
        addFormRow(formPanel, gbc, 2, "Tanggal", dateSpinner);
        addFormRow(formPanel, gbc, 3, "Pertemuan", pertemuanCombo);
        addFormRow(formPanel, gbc, 4, "Status Form", activeCombo);
        addFormRow(formPanel, gbc, 5, "Catatan", notesScroll);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        RoundedButton saveButton = new RoundedButton("Simpan", new Color(14, 116, 144), Color.WHITE, null);
        RoundedButton cancelButton = new RoundedButton("Batal", new Color(100, 116, 139), Color.WHITE, null);
        saveButton.setPreferredSize(new Dimension(110, 36));
        cancelButton.setPreferredSize(new Dimension(110, 36));

        saveButton.addActionListener(event -> {
            try {
                UserOption coach = (UserOption) coachCombo.getSelectedItem();
                LevelOption level = (LevelOption) levelCombo.getSelectedItem();
                String dateText = new SimpleDateFormat("yyyy-MM-dd").format((Date) dateSpinner.getValue());
                String pertemuan = String.valueOf(pertemuanCombo.getSelectedItem());
                boolean active = "Aktif".equals(String.valueOf(activeCombo.getSelectedItem()));

                if (existing == null) {
                    attendanceFormManagementService.createForm(
                            coach == null ? "" : coach.uuid,
                            level == null ? "" : level.uuid,
                            dateText,
                            pertemuan,
                            active,
                            notesArea.getText());
                    setStatusSuccess("Form absensi berhasil dibuat.");
                } else {
                    attendanceFormManagementService.updateForm(
                            existing,
                            coach == null ? "" : coach.uuid,
                            level == null ? "" : level.uuid,
                            dateText,
                            pertemuan,
                            active,
                            notesArea.getText());
                    setStatusSuccess("Form absensi berhasil diperbarui.");
                }

                dialog.dispose();
                loadData();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(dialog, exception.getMessage(), "Validasi Form Absensi", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> dialog.dispose());
        footer.add(cancelButton);
        footer.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private void selectCoach(JComboBox<UserOption> combo, String coachUuid) {
        if (coachUuid == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            UserOption option = combo.getItemAt(i);
            if (option != null && coachUuid.equals(option.uuid)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectLevel(JComboBox<LevelOption> combo, String levelUuid) {
        if (levelUuid == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            LevelOption option = combo.getItemAt(i);
            if (option != null && levelUuid.equals(option.uuid)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void deleteForm(AttendanceForm form) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Hapus form absensi coach " + form.getCoachName() + " tanggal "
                        + (form.getAttendanceDate() == null ? "-" : form.getAttendanceDate()) + " pertemuan ke-" + form.getPertemuanKe() + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            attendanceFormManagementService.deleteForm(form);
            setStatusSuccess("Form absensi berhasil dihapus.");
            loadData();
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
        }
    }

    private void openChecklistDialog(AttendanceForm selected) {
        List<AttendanceChecklistItem> rows;
        try {
            rows = attendanceFormManagementService.getChecklistByFormUuid(selected.getUuid());
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
            return;
        }

        JDialog dialog = new JDialog((Frame) null, "Cek List Absensi - " + selected.getCoachName(), true);
        dialog.setSize(900, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel checklistModel = new DefaultTableModel(
                new String[]{"Tanggal", "Pertemuan", "Coach", "Murid", "Class", "Status", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (AttendanceChecklistItem row : rows) {
            checklistModel.addRow(new Object[]{
                    row.getAttendanceDate() == null ? "-" : row.getAttendanceDate().toString(),
                    row.getPertemuanKe(),
                    safeText(row.getCoachName()),
                    safeText(row.getMuridName()),
                    safeText(row.getLevelName()),
                    safeText(row.getStatus()),
                    safeText(row.getNotes())
            });
        }

        JTable checklistTable = new JTable(checklistModel);
        checklistTable.setRowHeight(32);
        checklistTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        checklistTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        checklistTable.setGridColor(new Color(226, 232, 240));
        checklistTable.setShowVerticalLines(false);

        JLabel title = new JLabel("Total checklist: " + rows.size(), SwingConstants.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(new Color(30, 64, 175));

        dialog.add(title, BorderLayout.NORTH);
        dialog.add(new JScrollPane(checklistTable), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private boolean canCreate() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canCreate());
    }

    private boolean canUpdate() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canUpdate());
    }

    private boolean canDelete() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canDelete());
    }

    private String safeText(String value) {
        return value == null ? "-" : value;
    }

    private void setStatusNeutral(String message) {
        statusLabel.setOpaque(false);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        statusLabel.setForeground(new Color(71, 85, 105));
        statusLabel.setText(message);
    }

    private void setStatusSuccess(String message) {
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(220, 252, 231));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        statusLabel.setForeground(new Color(22, 163, 74));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setText(message);
    }

    private void setStatusError(String message) {
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(254, 226, 226));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        statusLabel.setForeground(new Color(220, 38, 38));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setText(message);
    }

    private static final class UserOption {
        private final String uuid;
        private final String name;

        private UserOption(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        @Override
        public String toString() {
            return name == null || name.trim().isEmpty() ? "-" : name;
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
            return name == null || name.trim().isEmpty() ? "-" : name;
        }
    }
}
