package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.AttendanceForm;
import com.tugasbesar.app.model.AttendanceRecord;
import com.tugasbesar.app.model.Level;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.AttendanceManagementService;
import com.tugasbesar.app.ui.component.RoundedButton;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class CoachAttendanceScreen extends JPanel {
    private final User currentUser;
    private final AppModule modulePermission;
    private final AttendanceManagementService attendanceManagementService;

    private final List<User> coachUsers;
    private final List<Level> levels;
    private final List<User> muridUsers;
    private final List<AttendanceRecord> existingRecords;

    private final JLabel statusLabel;
    private final JLabel dataInfoLabel;
    private final JLabel activeMonthLabel;
    private final JComboBox<UserOption> coachCombo;
    private final JComboBox<String> meetingCombo;
    private final JComboBox<LevelOption> levelCombo;
    private final DefaultTableModel tableModel;
    private final JTable muridTable;
    private final List<AttendanceForm> activeForms;
    private AttendanceForm selectedForm;

    public CoachAttendanceScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.attendanceManagementService = new AttendanceManagementService();
        this.coachUsers = attendanceManagementService.getCoachUsers();
        this.levels = attendanceManagementService.getAllLevels();
        this.muridUsers = new ArrayList<>();
        this.existingRecords = new ArrayList<>();
        this.statusLabel = new JLabel(" ");
        this.dataInfoLabel = new JLabel("Data ditampilkan: 0 murid");
        this.activeMonthLabel = new JLabel();
        this.coachCombo = new JComboBox<>();
        this.meetingCombo = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8"});
        this.levelCombo = new JComboBox<>();
        this.activeForms = new ArrayList<>();
        this.selectedForm = null;
        this.tableModel = new DefaultTableModel(new String[]{"Murid UUID", "Murid", "Class", "Hadir", "Catatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        this.muridTable = new JTable(tableModel);

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        initTopFilters();
        configureTable();

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        loadFormsAndTable();
    }

    private JPanel buildTopSection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);
        RoundedButton checkAllButton = createActionButton("\u2714 Centang Semua", new Color(14, 116, 144), 170);
        RoundedButton uncheckAllButton = createActionButton("\u2716 Kosongkan", new Color(100, 116, 139), 150);
        RoundedButton saveButton = createActionButton("\u2714 Simpan Absensi", new Color(22, 163, 74), 182);

        checkAllButton.addActionListener(event -> setAllChecked(true));
        uncheckAllButton.addActionListener(event -> setAllChecked(false));
        saveButton.addActionListener(event -> saveAttendance());

        controlPanel.add(new JLabel("Coach"));
        controlPanel.add(coachCombo);
        controlPanel.add(new JLabel("Pertemuan"));
        controlPanel.add(meetingCombo);
        controlPanel.add(new JLabel("Class"));
        controlPanel.add(levelCombo);
        controlPanel.add(checkAllButton);
        controlPanel.add(uncheckAllButton);
        controlPanel.add(saveButton);

        wrapper.add(controlPanel);
        wrapper.add(Box.createVerticalStrut(8));
        wrapper.add(activeMonthLabel);
        return wrapper;
    }

    private RoundedButton createActionButton(String text, Color background, int width) {
        RoundedButton button = new RoundedButton(text, background, Color.WHITE, null);
        button.setPreferredSize(new Dimension(width, 38));
        button.setMaximumSize(new Dimension(width, 38));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    private JPanel buildTableSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        statusPanel.add(statusLabel);
        panel.add(statusPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(muridTable);
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

    private void initTopFilters() {
        coachCombo.removeAllItems();
        if (isCoachUser()) {
            coachCombo.addItem(new UserOption(currentUser.getUuid(), currentUser.getFullName()));
            coachCombo.setEnabled(false);
        } else {
            coachCombo.addItem(new UserOption("", "Pilih Coach"));
            for (User coach : coachUsers) {
                coachCombo.addItem(new UserOption(coach.getUuid(), coach.getFullName()));
            }
        }
        coachCombo.setPreferredSize(new Dimension(170, 34));
        meetingCombo.setPreferredSize(new Dimension(120, 34));

        levelCombo.removeAllItems();
        levelCombo.addItem(new LevelOption("", "Pilih Class"));
        for (Level level : levels) {
            levelCombo.addItem(new LevelOption(level.getUuid(), level.getName()));
        }
        levelCombo.setPreferredSize(new Dimension(170, 34));

        coachCombo.addActionListener(event -> loadFormsAndTable());
        meetingCombo.addActionListener(event -> loadTable());
        levelCombo.addActionListener(event -> loadTable());

        LocalDate now = LocalDate.now();
        activeMonthLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        activeMonthLabel.setForeground(new Color(30, 64, 175));
        activeMonthLabel.setText("Bulan aktif absensi: " + String.format("%02d-%d", now.getMonthValue(), now.getYear()));
    }

    private void configureTable() {
        muridTable.setRowHeight(34);
        muridTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        muridTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        muridTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        muridTable.setGridColor(new Color(226, 232, 240));
        muridTable.setShowVerticalLines(false);
        muridTable.getColumnModel().getColumn(0).setMinWidth(0);
        muridTable.getColumnModel().getColumn(0).setMaxWidth(0);
        muridTable.getColumnModel().getColumn(0).setWidth(0);
        muridTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        muridTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        muridTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        muridTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setSelected(Boolean.TRUE.equals(value));
            checkBox.setOpaque(true);
            checkBox.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return checkBox;
        });
    }

    private void loadFormsAndTable() {
        try {
            activeForms.clear();
            selectedForm = null;

            UserOption coach = (UserOption) coachCombo.getSelectedItem();
            String coachUuid = coach == null ? "" : coach.uuid;
            if (coachUuid == null || coachUuid.trim().isEmpty()) {
                loadTable();
                return;
            }

            activeForms.addAll(attendanceManagementService.getActiveFormsByCoach(coachUuid));
            loadTable();
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            existingRecords.clear();
            muridUsers.clear();
            selectedForm = null;

            UserOption coach = (UserOption) coachCombo.getSelectedItem();
            LevelOption level = (LevelOption) levelCombo.getSelectedItem();
            String meetingText = (String) meetingCombo.getSelectedItem();

            if (coach == null || coach.uuid == null || coach.uuid.trim().isEmpty()
                    || level == null || level.uuid == null || level.uuid.trim().isEmpty()
                    || meetingText == null || meetingText.trim().isEmpty()) {
                dataInfoLabel.setText("Data ditampilkan: 0 murid");
                setStatusNeutral("Pilih coach, pertemuan, dan class.");
                return;
            }

            int pertemuan = Integer.parseInt(meetingText);
            selectedForm = findSelectedForm(coach.uuid, level.uuid, pertemuan);
            if (selectedForm == null) {
                dataInfoLabel.setText("Data ditampilkan: 0 murid");
                setStatusError("Form absensi bulan aktif tidak ditemukan untuk kombinasi pertemuan/class ini.");
                return;
            }

            muridUsers.addAll(attendanceManagementService.getMuridUsersByLevelUuid(level.uuid));
            existingRecords.addAll(attendanceManagementService.getFormAttendanceForMeeting(
                    selectedForm.getUuid(), coach.uuid, level.uuid, selectedForm.getAttendanceDate().toString(), selectedForm.getPertemuanKe()));

            for (User murid : muridUsers) {
                AttendanceRecord existing = findExistingByStudent(murid.getUuid());
                boolean hadir = existing != null && "Hadir".equalsIgnoreCase(existing.getStatus());
                tableModel.addRow(new Object[]{
                        murid.getUuid(),
                        murid.getFullName(),
                        murid.getLevelName() == null ? "-" : murid.getLevelName(),
                        hadir,
                        existing == null ? "" : safeText(existing.getNotes())
                });
            }

            dataInfoLabel.setText("Data ditampilkan: " + muridUsers.size() + " murid");
            setStatusNeutral("Form aktif: " + selectedForm.getAttendanceDate() + " | Pertemuan " + selectedForm.getPertemuanKe() + " | " + safeText(selectedForm.getLevelName()));
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
        }
    }

    private AttendanceForm findSelectedForm(String coachUuid, String levelUuid, int pertemuan) {
        LocalDate now = LocalDate.now();
        AttendanceForm picked = null;
        for (AttendanceForm form : activeForms) {
            if (form == null || form.getAttendanceDate() == null || !form.isActive()) {
                continue;
            }
            if (!coachUuid.equals(form.getCoachUuid())) {
                continue;
            }
            if (!levelUuid.equals(form.getLevelUuid())) {
                continue;
            }
            if (pertemuan != form.getPertemuanKe()) {
                continue;
            }
            LocalDate formDate = form.getAttendanceDate();
            if (formDate.getYear() != now.getYear() || formDate.getMonthValue() != now.getMonthValue()) {
                continue;
            }
            if (picked == null || formDate.isAfter(picked.getAttendanceDate())) {
                picked = form;
            }
        }
        return picked;
    }

    private AttendanceRecord findExistingByStudent(String muridUuid) {
        for (AttendanceRecord record : existingRecords) {
            if (muridUuid.equals(record.getMuridUuid())) {
                return record;
            }
        }
        return null;
    }

    private void setAllChecked(boolean checked) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            tableModel.setValueAt(checked, row, 3);
        }
    }

    private void saveAttendance() {
        if (!canCreate() && !canUpdate()) {
            setStatusError("Anda tidak memiliki izin menyimpan absensi.");
            return;
        }
        try {
            UserOption coach = (UserOption) coachCombo.getSelectedItem();
            LevelOption level = (LevelOption) levelCombo.getSelectedItem();
            int pertemuan = Integer.parseInt((String) meetingCombo.getSelectedItem());
            if (coach == null || coach.uuid == null || coach.uuid.trim().isEmpty()
                    || level == null || level.uuid == null || level.uuid.trim().isEmpty()) {
                throw new IllegalArgumentException("Pilih coach, pertemuan, dan class terlebih dahulu.");
            }

            AttendanceForm targetForm = selectedForm != null ? selectedForm : findSelectedForm(coach.uuid, level.uuid, pertemuan);
            if (targetForm == null) {
                throw new IllegalArgumentException("Form absensi bulan aktif untuk kombinasi ini tidak ditemukan.");
            }

            int affected = 0;
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String muridUuid = String.valueOf(tableModel.getValueAt(row, 0));
                boolean hadir = Boolean.TRUE.equals(tableModel.getValueAt(row, 3));
                String notes = safeText(String.valueOf(tableModel.getValueAt(row, 4)));
                String status = hadir ? "Hadir" : "Alpha";
                AttendanceRecord existing = findExistingByStudent(muridUuid);
                if (existing == null) {
                    existing = attendanceManagementService.findExistingSlot(
                            coach.uuid,
                            muridUuid,
                            targetForm.getAttendanceDate().toString(),
                            pertemuan);
                }
                if (existing == null) {
                    attendanceManagementService.createAttendance(
                            targetForm.getUuid(), coach.uuid, muridUuid, level.uuid,
                            targetForm.getAttendanceDate().toString(), pertemuan, status, notes);
                } else {
                    attendanceManagementService.updateAttendance(
                            existing, targetForm.getUuid(), coach.uuid, muridUuid, level.uuid,
                            targetForm.getAttendanceDate().toString(), pertemuan, status, notes);
                }
                affected++;
            }
            loadTable();
            setStatusSuccess("Absensi level tersimpan. Total murid diproses: " + affected);
        } catch (Exception exception) {
            setStatusError(exception.getMessage());
        }
    }

    private boolean isCoachUser() {
        if (currentUser == null || currentUser.getRole() == null) {
            return false;
        }
        String role = currentUser.getRole().toLowerCase();
        return role.contains("coach")
                || role.contains("pelatih")
                || role.contains("trainer")
                || role.contains("instruktur");
    }

    private boolean canCreate() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canCreate());
    }

    private boolean canUpdate() {
        return (currentUser != null && currentUser.isSuperAdmin()) || (modulePermission != null && modulePermission.canUpdate());
    }

    private String safeText(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return "";
        }
        return value.trim();
    }

    private void setStatusNeutral(String message) {
        statusLabel.setOpaque(false);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        statusLabel.setForeground(new Color(71, 85, 105));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
