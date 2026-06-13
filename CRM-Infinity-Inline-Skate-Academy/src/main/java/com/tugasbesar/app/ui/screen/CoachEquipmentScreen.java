package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Equipment;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.EquipmentManagementService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CoachEquipmentScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private final User currentUser;
    private final AppModule modulePermission;
    private final EquipmentManagementService service;
    private final List<Equipment> equipments;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;

    public CoachEquipmentScreen(User currentUser, AppModule modulePermission) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.service = new EquipmentManagementService();
        this.equipments = new ArrayList<>();
        this.tableModel = new DefaultTableModel(new String[]{"Nama", "Jenis", "Ukuran", "Jumlah", "Kondisi", "Status", "Keterangan", "Updated"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.searchField = new JTextField();

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildTop() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel info = new JLabel("Daftar peralatan tersedia untuk kebutuhan coach. Halaman ini hanya untuk lihat data.");
        info.setFont(new Font("SansSerif", Font.PLAIN, 13));
        info.setForeground(new Color(71, 85, 105));
        info.setAlignmentX(LEFT_ALIGNMENT);

        searchField.setMaximumSize(new Dimension(360, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setAlignmentX(LEFT_ALIGNMENT);
        searchField.addActionListener(event -> applyFilter());

        panel.add(info);
        panel.add(Box.createVerticalStrut(8));
        panel.add(searchField);
        return panel;
    }

    private void configureTable() {
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
    }

    private void loadData() {
        equipments.clear();
        equipments.addAll(service.getAllEquipment());
        applyFilter();
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        for (Equipment item : equipments) {
            String blob = (safe(item.getName()) + " " + safe(item.getType()) + " " + safe(item.getSize()) + " "
                    + safe(item.getCondition()) + " " + safe(item.getStatus()) + " " + safe(item.getNotes())).toLowerCase();
            if (!keyword.isEmpty() && !blob.contains(keyword)) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    safe(item.getName()),
                    safe(item.getType()),
                    safe(item.getSize()),
                    item.getQuantity(),
                    safe(item.getCondition()),
                    safe(item.getStatus()),
                    safe(item.getNotes()),
                    item.getUpdatedAt() == null ? "-" : DATE_TIME_FORMATTER.format(item.getUpdatedAt())
            });
        }
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
