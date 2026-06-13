package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.RoleModuleAssignment;
import com.tugasbesar.app.repository.AccessControlRepository;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

public class RoleModuleScreen extends JPanel {

    public RoleModuleScreen() {
        AccessControlRepository accessControlRepository = new AccessControlRepository();
        List<RoleModuleAssignment> assignments = accessControlRepository.findRoleModuleAssignments();

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Role Module");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(15, 23, 42));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("<html>Halaman ini menampilkan semua relasi role dan module yang sudah aktif di database. Jadi admin bisa cepat paham role mana punya akses ke menu apa saja.</html>");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 116, 139));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel infoLabel = new JLabel("Total relasi aktif: " + assignments.size());
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        infoLabel.setForeground(new Color(14, 116, 144));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columns = {"Role UUID", "Nama Role", "Module UUID", "Nama Module", "View", "Create", "Update", "Delete", "Export", "Import", "Deskripsi"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (RoleModuleAssignment assignment : assignments) {
            tableModel.addRow(new Object[]{
                    assignment.getRoleCode(),
                    assignment.getRoleName(),
                    assignment.getModuleCode(),
                    assignment.getModuleName(),
                    assignment.canView() ? "Ya" : "Tidak",
                    assignment.canCreate() ? "Ya" : "Tidak",
                    assignment.canUpdate() ? "Ya" : "Tidak",
                    assignment.canDelete() ? "Ya" : "Tidak",
                    assignment.canExport() ? "Ya" : "Tidak",
                    assignment.canImport() ? "Ya" : "Tidak",
                    assignment.getModuleDescription()
            });
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(infoLabel);
        content.add(Box.createVerticalStrut(20));
        content.add(scrollPane);

        add(content, BorderLayout.CENTER);
    }
}
