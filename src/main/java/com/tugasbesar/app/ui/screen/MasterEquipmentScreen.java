package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AppModule;
import com.tugasbesar.app.model.Equipment;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.service.EquipmentManagementService;
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MasterEquipmentScreen extends JPanel {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    private static final String ADD_ICON = "\u271A";
    private static final String REFRESH_ICON = "\u21BB";
    private static final String EXPORT_ICON = "\u21E9";
    private static final String PDF_ICON = "\uD83D\uDCC4";
    private static final String IMPORT_ICON = "\u21E7";
    private static final String EDIT_ICON = "\u270E";
    private static final String DELETE_ICON = "\u2716";
    private static final String SEARCH_ICON = "\u2315";
    private static final String CLEAR_ICON = "\u2715";

    private final User currentUser;
    private final AppModule modulePermission;
    private final EquipmentManagementService equipmentManagementService;
    private final List<Equipment> sourceItems;
    private final DefaultTableModel tableModel;
    private final JTable equipmentTable;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final JLabel statusLabel;
    private final JLabel dataInfoLabel;
    private final JTextField searchField;
    private final Runnable sessionRefreshAction;

    public MasterEquipmentScreen(User currentUser, AppModule modulePermission, Runnable sessionRefreshAction) {
        this.currentUser = currentUser;
        this.modulePermission = modulePermission;
        this.sessionRefreshAction = sessionRefreshAction;
        this.equipmentManagementService = new EquipmentManagementService();
        this.sourceItems = new ArrayList<>();
        this.tableModel = new DefaultTableModel(
                new String[]{"UUID", "Nama", "Jenis", "Ukuran", "Jumlah", "Kondisi", "Status", "Keterangan", "Updated", "Action"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.equipmentTable = new JTable(tableModel);
        this.rowSorter = new TableRowSorter<>(tableModel);
        this.statusLabel = new JLabel(" ");
        this.dataInfoLabel = new JLabel("Data ditampilkan: 0");
        this.searchField = new JTextField();

        setLayout(new BorderLayout(0, 8));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));

        configureTable();
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
        RoundedButton addButton = createActionButton(ADD_ICON + " Tambah", new Color(14, 116, 144), 132);
        RoundedButton refreshButton = createActionButton(REFRESH_ICON + " Refresh", new Color(71, 85, 105), 142);
        RoundedButton exportButton = createActionButton(EXPORT_ICON + " Export Excel", new Color(22, 163, 74), 176);
        RoundedButton pdfButton = createActionButton(PDF_ICON + " Export PDF", new Color(220, 38, 38), 164);
        RoundedButton importButton = createActionButton(IMPORT_ICON + " Import", new Color(22, 163, 74), 154);
        RoundedButton searchButton = createActionButton(SEARCH_ICON + " Search", new Color(30, 64, 175), 126);
        RoundedButton clearButton = createActionButton(CLEAR_ICON + " Clear", new Color(100, 116, 139), 126);

        addButton.setEnabled(canCreate());
        addButton.addActionListener(event -> openEquipmentDialog(null));
        refreshButton.addActionListener(event -> loadData());
        exportButton.setEnabled(canExport());
        exportButton.addActionListener(event -> exportData());
        pdfButton.setEnabled(canExport());
        pdfButton.addActionListener(event -> exportPdf());
        importButton.setEnabled(canImport());
        importButton.addActionListener(event -> importData());
        searchButton.addActionListener(event -> applyFilters());
        clearButton.addActionListener(event -> {
            searchField.setText("Cari nama, jenis, ukuran, kondisi, status...");
            applyFilters();
        });

        actionPanel.add(addButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(exportButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(pdfButton);
        actionPanel.add(Box.createHorizontalStrut(8));
        actionPanel.add(importButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);
        searchField.setPreferredSize(new Dimension(360, 34));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Cari berdasarkan nama, jenis, ukuran, kondisi, status, atau keterangan");
        searchField.setText("Cari nama, jenis, ukuran, kondisi, status...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent event) {
                if ("Cari nama, jenis, ukuran, kondisi, status...".equals(searchField.getText())) {
                    searchField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent event) {
                if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                    searchField.setText("Cari nama, jenis, ukuran, kondisi, status...");
                }
            }
        });
        searchField.addActionListener(event -> applyFilters());

        filterPanel.add(new JLabel("Search"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrapper.add(actionPanel);
        wrapper.add(Box.createVerticalStrut(16));
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

        JScrollPane scrollPane = new JScrollPane(equipmentTable);
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
        equipmentTable.setRowHeight(34);
        equipmentTable.setRowSorter(rowSorter);
        equipmentTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        equipmentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        equipmentTable.getTableHeader().setReorderingAllowed(true);
        equipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipmentTable.setGridColor(new Color(226, 232, 240));
        equipmentTable.setShowVerticalLines(false);
        equipmentTable.getColumnModel().getColumn(0).setMinWidth(0);
        equipmentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        equipmentTable.getColumnModel().getColumn(0).setWidth(0);
        equipmentTable.getColumnModel().getColumn(9).setPreferredWidth(170);
        equipmentTable.getColumnModel().getColumn(9).setCellRenderer(new ActionCellRenderer());

        equipmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int viewRow = equipmentTable.rowAtPoint(event.getPoint());
                int viewColumn = equipmentTable.columnAtPoint(event.getPoint());
                if (viewRow < 0 || viewColumn != 9) {
                    return;
                }

                int modelRow = equipmentTable.convertRowIndexToModel(viewRow);
                Equipment selected = findByUuid(String.valueOf(tableModel.getValueAt(modelRow, 0)));
                if (selected == null) {
                    return;
                }

                int cellX = equipmentTable.getCellRect(viewRow, viewColumn, false).x;
                int cellWidth = equipmentTable.getCellRect(viewRow, viewColumn, false).width;
                int relativeX = event.getX() - cellX;
                if (relativeX < cellWidth / 2) {
                    if (!canUpdate()) {
                        showStatus("Anda tidak punya izin update data.", true);
                        return;
                    }
                    openEquipmentDialog(selected);
                    return;
                }
                if (!canDelete()) {
                    showStatus("Anda tidak punya izin delete data.", true);
                    return;
                }
                deleteEquipment(selected);
            }
        });
    }

    private void loadData() {
        sourceItems.clear();
        sourceItems.addAll(equipmentManagementService.getAllEquipment());
        applyFilters();
    }

    private void applyFilters() {
        tableModel.setRowCount(0);
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        if ("cari nama, jenis, ukuran, kondisi, status...".equals(keyword)) {
            keyword = "";
        }

        int displayed = 0;
        for (Equipment item : sourceItems) {
            String blob = (safe(item.getName()) + " " + safe(item.getType()) + " " + safe(item.getSize()) + " "
                    + safe(item.getCondition()) + " " + safe(item.getStatus()) + " " + safe(item.getNotes())).toLowerCase();
            if (!keyword.isEmpty() && !blob.contains(keyword)) {
                continue;
            }
            tableModel.addRow(new Object[]{
                    item.getUuid(),
                    safe(item.getName()),
                    safe(item.getType()),
                    safe(item.getSize()),
                    item.getQuantity(),
                    safe(item.getCondition()),
                    safe(item.getStatus()),
                    safe(item.getNotes()),
                    item.getUpdatedAt() == null ? "-" : DATE_TIME_FORMATTER.format(item.getUpdatedAt()),
                    ""
            });
            displayed++;
        }
        dataInfoLabel.setText("Data ditampilkan: " + displayed);
    }

    private Equipment findByUuid(String uuid) {
        for (Equipment item : sourceItems) {
            if (item.getUuid().equals(uuid)) {
                return item;
            }
        }
        return null;
    }

    private void openEquipmentDialog(Equipment editing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), editing == null ? "Tambah Peralatan" : "Edit Peralatan", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JTextField nameField = createTextField();
        JTextField typeField = createTextField();
        JTextField sizeField = createTextField();
        JTextField quantityField = createTextField();
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"Baik", "Cukup", "Rusak Ringan", "Rusak Berat"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tersedia", "Dipinjam", "Perbaikan", "Tidak Aktif"});
        JTextArea notesArea = new JTextArea(4, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(360, 96));
        notesScroll.setMaximumSize(new Dimension(360, 96));
        notesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        conditionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        conditionCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noteLabel = new JLabel(" ");
        noteLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteLabel.setForeground(new Color(71, 85, 105));
        noteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (editing != null) {
            nameField.setText(editing.getName());
            typeField.setText(editing.getType());
            sizeField.setText(editing.getSize());
            quantityField.setText(String.valueOf(editing.getQuantity()));
            conditionCombo.setSelectedItem(editing.getCondition());
            statusCombo.setSelectedItem(editing.getStatus());
            notesArea.setText(editing.getNotes());
            noteLabel.setText("Perbarui data peralatan.");
        } else {
            quantityField.setText("0");
            noteLabel.setText("Masukkan data master peralatan sepatu roda.");
        }

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(createLabel("Nama Peralatan"));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Jenis"));
        formPanel.add(typeField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Ukuran"));
        formPanel.add(sizeField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Jumlah"));
        formPanel.add(quantityField);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Kondisi"));
        formPanel.add(conditionCombo);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Status"));
        formPanel.add(statusCombo);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(createLabel("Keterangan"));
        formPanel.add(notesScroll);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(noteLabel);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        RoundedButton cancelButton = createActionButton("\u2715 Batal", new Color(100, 116, 139), 122);
        RoundedButton saveButton = createActionButton(editing == null ? "\u2714 Simpan" : "\u27F3 Update", new Color(14, 116, 144), 122);
        cancelButton.addActionListener(event -> dialog.dispose());
        saveButton.addActionListener(event -> {
            try {
                if (editing == null) {
                    equipmentManagementService.createEquipment(
                            nameField.getText(),
                            typeField.getText(),
                            sizeField.getText(),
                            quantityField.getText(),
                            String.valueOf(conditionCombo.getSelectedItem()),
                            String.valueOf(statusCombo.getSelectedItem()),
                            notesArea.getText());
                } else {
                    equipmentManagementService.updateEquipment(
                            editing,
                            nameField.getText(),
                            typeField.getText(),
                            sizeField.getText(),
                            quantityField.getText(),
                            String.valueOf(conditionCombo.getSelectedItem()),
                            String.valueOf(statusCombo.getSelectedItem()),
                            notesArea.getText());
                }
                loadData();
                showStatus(editing == null ? "Peralatan berhasil ditambahkan." : "Peralatan berhasil diperbarui.", false);
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
        dialog.setSize(520, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void deleteEquipment(Equipment equipment) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus peralatan " + equipment.getName() + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            equipmentManagementService.deleteEquipment(equipment);
            loadData();
            showStatus("Peralatan berhasil dihapus.", false);
        } catch (Exception exception) {
            showStatus(exception.getMessage(), true);
        }
    }

    private void exportData() {
        if (!canExport()) {
            showStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Peralatan (Excel)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = normalizeChosenFile(chooser, true);
        if (file == null) {
            showStatus("Lokasi file export tidak valid.", true);
            return;
        }
        try {
            String name = file.getName().toLowerCase();
            if (!name.endsWith(".xlsx")) {
                file = appendExtension(file, "xlsx");
            }
            exportXlsx(file);
        } catch (Exception exception) {
            showStatus("Gagal export: " + exception.getMessage(), true);
        }
    }

    private void exportPdf() {
        if (!canExport()) {
            showStatus("Anda tidak punya izin export.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Master Peralatan (PDF)");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF File (*.pdf)", "pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file == null) {
            showStatus("Lokasi file export tidak valid.", true);
            return;
        }
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = appendExtension(file, "pdf");
        }
        try {
            exportJasperPdf(file);
            showStatus("Export PDF berhasil.", false);
        } catch (NoClassDefFoundError error) {
            showStatus("Library Jasper belum lengkap: " + error.getMessage(), true);
        } catch (Exception exception) {
            showStatus("Gagal export PDF: " + rootErrorMessage(exception), true);
        }
    }

    private void exportJasperPdf(File file) throws JRException {
        List<EquipmentExportRow> rows = new ArrayList<>();
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            rows.add(new EquipmentExportRow(
                    String.valueOf(tableModel.getValueAt(row, 1)),
                    String.valueOf(tableModel.getValueAt(row, 2)),
                    String.valueOf(tableModel.getValueAt(row, 3)),
                    String.valueOf(tableModel.getValueAt(row, 4)),
                    String.valueOf(tableModel.getValueAt(row, 5)),
                    String.valueOf(tableModel.getValueAt(row, 6)),
                    String.valueOf(tableModel.getValueAt(row, 7)),
                    String.valueOf(tableModel.getValueAt(row, 8))
            ));
        }

        String jrxml = buildEquipmentJasperTemplate();
        InputStream templateStream = new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8));
        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
    }

    private String rootErrorMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.trim().isEmpty() ? current.getClass().getSimpleName() : message;
    }

    private String buildEquipmentJasperTemplate() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports "
                + "http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" "
                + "name=\"master_equipment_report\" pageWidth=\"842\" pageHeight=\"595\" orientation=\"Landscape\" "
                + "columnWidth=\"802\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">"
                + "<field name=\"name\" class=\"java.lang.String\"/>"
                + "<field name=\"type\" class=\"java.lang.String\"/>"
                + "<field name=\"size\" class=\"java.lang.String\"/>"
                + "<field name=\"quantity\" class=\"java.lang.String\"/>"
                + "<field name=\"condition\" class=\"java.lang.String\"/>"
                + "<field name=\"status\" class=\"java.lang.String\"/>"
                + "<field name=\"notes\" class=\"java.lang.String\"/>"
                + "<field name=\"updated\" class=\"java.lang.String\"/>"
                + "<title><band height=\"36\">"
                + "<staticText><reportElement x=\"0\" y=\"0\" width=\"802\" height=\"28\"/>"
                + "<textElement><font size=\"14\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[Master Peralatan Export]]></text></staticText>"
                + "</band></title>"
                + "<columnHeader><band height=\"22\">"
                + buildHeaderText(0, 120, "Nama")
                + buildHeaderText(120, 100, "Jenis")
                + buildHeaderText(220, 70, "Ukuran")
                + buildHeaderText(290, 60, "Jumlah")
                + buildHeaderText(350, 90, "Kondisi")
                + buildHeaderText(440, 90, "Status")
                + buildHeaderText(530, 160, "Keterangan")
                + buildHeaderText(690, 112, "Updated")
                + "</band></columnHeader>"
                + "<detail><band height=\"20\">"
                + buildDetailTextField(0, 120, "name")
                + buildDetailTextField(120, 100, "type")
                + buildDetailTextField(220, 70, "size")
                + buildDetailTextField(290, 60, "quantity")
                + buildDetailTextField(350, 90, "condition")
                + buildDetailTextField(440, 90, "status")
                + buildDetailTextField(530, 160, "notes")
                + buildDetailTextField(690, 112, "updated")
                + "</band></detail>"
                + "</jasperReport>";
    }

    private String buildHeaderText(int x, int width, String text) {
        return "<staticText><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"20\"/>"
                + "<textElement><font size=\"10\" isBold=\"true\"/></textElement>"
                + "<text><![CDATA[" + text + "]]></text></staticText>";
    }

    private String buildDetailTextField(int x, int width, String field) {
        return "<textField textAdjust=\"StretchHeight\"><reportElement x=\"" + x + "\" y=\"0\" width=\"" + width + "\" height=\"18\"/>"
                + "<textElement><font size=\"9\"/></textElement>"
                + "<textFieldExpression><![CDATA[$F{" + field + "}]]></textFieldExpression></textField>";
    }

    private void importData() {
        if (!canImport()) {
            showStatus("Anda tidak punya izin import.", true);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import Master Peralatan");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV File (*.csv)", "csv"));
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = normalizeChosenFile(chooser, false);
        if (file == null || !file.exists()) {
            showStatus("File import tidak ditemukan.", true);
            return;
        }
        String name = file.getName().toLowerCase();
        if (name.endsWith(".xlsx")) {
            importXlsx(file);
        } else if (name.endsWith(".csv")) {
            importCsv(file);
        } else {
            showStatus("Format file tidak didukung. Gunakan CSV atau XLSX.", true);
        }
    }

    private void exportCsv(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("nama,jenis,ukuran,jumlah,kondisi,status,keterangan");
            writer.newLine();
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 1))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 2))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 3))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 4))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 5))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 6))) + ",");
                writer.write(csvEscape(String.valueOf(tableModel.getValueAt(row, 7))));
                writer.newLine();
            }
        } catch (Exception exception) {
            showStatus("Gagal export CSV: " + exception.getMessage(), true);
        }
    }

    private void importCsv(File file) {
        int success = 0;
        int failed = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = parseCsvLine(line);
                if (parts.length < 6) {
                    failed++;
                    continue;
                }
                try {
                    equipmentManagementService.createEquipment(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts.length > 2 ? parts[2].trim() : "",
                            parts.length > 3 ? parts[3].trim() : "0",
                            parts.length > 4 ? parts[4].trim() : "Baik",
                            parts.length > 5 ? parts[5].trim() : "Tersedia",
                            parts.length > 6 ? parts[6].trim() : "");
                    success++;
                } catch (Exception exception) {
                    failed++;
                }
            }
            loadData();
            if (failed > 0) {
                showStatus("Import selesai. Berhasil: " + success + ", gagal: " + failed, true);
            }
        } catch (Exception exception) {
            showStatus("Gagal import CSV: " + exception.getMessage(), true);
        }
    }

    private void exportXlsx(File file) {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"nama", "jenis", "ukuran", "jumlah", "kondisi", "status", "keterangan"});
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            rows.add(new String[]{
                    String.valueOf(tableModel.getValueAt(row, 1)),
                    String.valueOf(tableModel.getValueAt(row, 2)),
                    String.valueOf(tableModel.getValueAt(row, 3)),
                    String.valueOf(tableModel.getValueAt(row, 4)),
                    String.valueOf(tableModel.getValueAt(row, 5)),
                    String.valueOf(tableModel.getValueAt(row, 6)),
                    String.valueOf(tableModel.getValueAt(row, 7))
            });
        }

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
                            + "<sheets><sheet name=\"Peralatan\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                            + "</Relationships>");
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", buildSheetXml(rows));
        } catch (Exception exception) {
            throw new RuntimeException("Gagal membuat XLSX: " + exception.getMessage(), exception);
        }
    }

    private void importXlsx(File file) {
        int success = 0;
        int failed = 0;
        try {
            List<String[]> rows = readSheetRows(file);
            for (String[] parts : rows) {
                if (parts.length < 6) {
                    failed++;
                    continue;
                }
                if ("nama".equalsIgnoreCase(parts[0])) {
                    continue;
                }
                try {
                    equipmentManagementService.createEquipment(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts.length > 2 ? parts[2].trim() : "",
                            parts.length > 3 ? parts[3].trim() : "0",
                            parts.length > 4 ? parts[4].trim() : "Baik",
                            parts.length > 5 ? parts[5].trim() : "Tersedia",
                            parts.length > 6 ? parts[6].trim() : "");
                    success++;
                } catch (Exception exception) {
                    failed++;
                }
            }
            loadData();
            if (failed > 0) {
                showStatus("Import XLSX selesai. Berhasil: " + success + ", gagal: " + failed, true);
            }
        } catch (Exception exception) {
            showStatus("Gagal import XLSX: " + exception.getMessage(), true);
        }
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
        if (saveMode) {
            String lowerName = resolved.getName().toLowerCase();
            if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".csv")) {
                resolved = appendExtension(resolved, "xlsx");
            }
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
                    String ref = cell.getAttribute("r");
                    int col = columnIndex(ref);
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
        zip.write(content.getBytes("UTF-8"));
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

    private String csvEscape(String value) {
        String text = value == null ? "" : value;
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
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
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return label;
    }

    private final class ActionCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            panel.add(createActionBadge(EDIT_ICON + " Edit", new Color(14, 116, 144), "Edit data"));
            panel.add(createActionBadge(DELETE_ICON + " Delete", new Color(220, 38, 38), "Hapus data"));
            return panel;
        }
    }

    private void showStatus(String message, boolean error) {
        statusLabel.setText(message == null || message.trim().isEmpty() ? " " : message);
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(error ? new Color(220, 38, 38) : new Color(22, 163, 74));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(error ? new Color(254, 226, 226) : new Color(220, 252, 231));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private boolean canCreate() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canCreate();
    }

    private boolean canExport() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canExport();
    }

    private boolean canImport() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canImport();
    }

    private boolean canUpdate() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canUpdate();
    }

    private boolean canDelete() {
        return currentUser != null && currentUser.isSuperAdmin() || modulePermission != null && modulePermission.canDelete();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public static final class EquipmentExportRow {
        private final String name;
        private final String type;
        private final String size;
        private final String quantity;
        private final String condition;
        private final String status;
        private final String notes;
        private final String updated;

        public EquipmentExportRow(String name, String type, String size, String quantity, String condition, String status, String notes, String updated) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.quantity = quantity;
            this.condition = condition;
            this.status = status;
            this.notes = notes;
            this.updated = updated;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getSize() {
            return size;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getCondition() {
            return condition;
        }

        public String getStatus() {
            return status;
        }

        public String getNotes() {
            return notes;
        }

        public String getUpdated() {
            return updated;
        }
    }
}
