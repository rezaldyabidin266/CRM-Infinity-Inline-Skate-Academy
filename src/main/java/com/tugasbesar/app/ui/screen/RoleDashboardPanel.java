package com.tugasbesar.app.ui.screen;

import com.tugasbesar.app.model.AdminDashboardData;
import com.tugasbesar.app.model.CoachDashboardData;
import com.tugasbesar.app.model.DashboardActivityItem;
import com.tugasbesar.app.model.DashboardMetric;
import com.tugasbesar.app.model.StudentDashboardData;
import com.tugasbesar.app.model.User;
import com.tugasbesar.app.repository.DashboardRepository;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

public class RoleDashboardPanel extends JPanel {
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);
    private static final Color TITLE_COLOR = new Color(15, 23, 42);
    private static final Color SUBTITLE_COLOR = new Color(71, 85, 105);
    private static final Color ACCENT_ONE = new Color(14, 116, 144);
    private static final Color ACCENT_TWO = new Color(22, 163, 74);
    private static final Color ACCENT_THREE = new Color(249, 115, 22);
    private static final Color ACCENT_FOUR = new Color(30, 64, 175);

    private final User currentUser;
    private final DashboardRepository dashboardRepository;

    public RoleDashboardPanel(User currentUser) {
        this.currentUser = currentUser;
        this.dashboardRepository = new DashboardRepository();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        add(buildHeroSection());
        add(Box.createVerticalStrut(18));
        buildRoleContent();
    }

    private JPanel buildHeroSection() {
        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(new Color(15, 23, 42));
        hero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 41, 59)),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel greeting = new JLabel("Halo, " + safe(currentUser.getFullName(), "User"));
        greeting.setFont(new Font("SansSerif", Font.BOLD, 24));
        greeting.setForeground(Color.WHITE);
        greeting.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(getRoleSubtitle());
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(191, 219, 254));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(greeting);
        text.add(Box.createVerticalStrut(8));
        text.add(subtitle);

        hero.add(text, BorderLayout.CENTER);
        return hero;
    }

    private void buildRoleContent() {
        if (isCoach()) {
            CoachDashboardData data = dashboardRepository.loadCoachDashboardData(currentUser);
            add(buildMetricsGrid(data.getMetrics(), 3));
            add(Box.createVerticalStrut(18));
            add(buildTwoColumnSections(
                    buildActivityCard("Form Coach", "Form aktif dan jadwal terakhir Anda.", data.getActiveForms(), ACCENT_ONE),
                    buildActivityCard("Riwayat Absensi", "Aktivitas absensi murid yang Anda isi.", data.getRecentAttendance(), ACCENT_TWO)));
            return;
        }

        if (isStudent()) {
            StudentDashboardData data = dashboardRepository.loadStudentDashboardData(currentUser);
            add(buildMetricsGrid(data.getMetrics(), 3));
            add(Box.createVerticalStrut(18));
            add(buildTwoColumnSections(
                    buildActivityCard("Absensi Saya", "Riwayat hadir terbaru Anda.", data.getRecentAttendance(), ACCENT_FOUR),
                    buildActivityCard("Pembayaran Saya", "Status pembayaran pribadi terbaru.", data.getRecentPayments(), ACCENT_TWO)));
            return;
        }

        AdminDashboardData data = dashboardRepository.loadAdminDashboardData();
        add(buildMetricsGrid(data.getMetrics(), 4));
        add(Box.createVerticalStrut(18));
        add(buildThreeColumnSections(
                buildActivityCard("Form Absensi Terbaru", "Pantau form coach yang aktif dan baru diperbarui.", data.getRecentAttendanceForms(), ACCENT_ONE),
                buildActivityCard("Pembayaran Murid", "Status pembayaran murid bulan berjalan.", data.getRecentStudentPayments(), ACCENT_TWO),
                buildActivityCard("Pembayaran Coach", "Status gaji coach bulan berjalan.", data.getRecentCoachPayments(), ACCENT_THREE)));
    }

    private JPanel buildMetricsGrid(List<DashboardMetric> metrics, int columns) {
        int rows = (int) Math.ceil(metrics.size() / (double) columns);
        JPanel panel = new JPanel(new GridLayout(rows, columns, 14, 14));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rows * 126));

        Color[] accents = new Color[]{ACCENT_ONE, ACCENT_TWO, ACCENT_THREE, ACCENT_FOUR};
        for (int i = 0; i < metrics.size(); i++) {
            panel.add(buildMetricCard(metrics.get(i), accents[i % accents.length]));
        }
        return panel;
    }

    private JPanel buildMetricCard(DashboardMetric metric, Color accent) {
        MetricVisual visual = resolveMetricVisual(metric.getLabel(), accent);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel iconLabel = createChipLabel(visual, 54, 26);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JLabel label = new JLabel(metric.getLabel());
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(SUBTITLE_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel value = new JLabel(metric.getValue());
        value.setFont(new Font("SansSerif", Font.BOLD, 28));
        value.setForeground(TITLE_COLOR);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.add(label);
        textBlock.add(Box.createVerticalStrut(12));
        textBlock.add(value);

        JPanel body = new JPanel(new BorderLayout(16, 0));
        body.setOpaque(false);
        body.add(iconLabel, BorderLayout.WEST);
        body.add(textBlock, BorderLayout.CENTER);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityCard(String title, String subtitle, List<DashboardActivityItem> items, Color accent) {
        MetricVisual visual = resolveSectionVisual(title, accent);

        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = createChipLabel(visual, 24, 13);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(SUBTITLE_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(visual.color);
        accentBar.setPreferredSize(new Dimension(54, 4));
        accentBar.setMaximumSize(new Dimension(54, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleRow.add(iconLabel);
        titleRow.add(Box.createHorizontalStrut(10));
        titleRow.add(titleLabel);

        header.add(accentBar);
        header.add(Box.createVerticalStrut(10));
        header.add(titleRow);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (items == null || items.isEmpty()) {
            JLabel empty = new JLabel("Belum ada data.");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
            empty.setForeground(SUBTITLE_COLOR);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            list.add(empty);
        } else {
            for (DashboardActivityItem item : items) {
                list.add(buildActivityRow(item, visual));
                list.add(Box.createVerticalStrut(10));
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityRow(DashboardActivityItem item, MetricVisual sectionVisual) {
        MetricVisual visual = resolveRowVisual(item, sectionVisual);
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel iconLabel = createChipLabel(visual, 26, 14);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel text = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        text.setOpaque(false);

        JLabel title = new JLabel(safe(item.getTitle(), "-"));
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TITLE_COLOR);

        JLabel subtitle = new JLabel(safe(item.getSubtitle(), "-"));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(SUBTITLE_COLOR);

        JLabel meta = new JLabel(safe(item.getMeta(), "-"));
        meta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        meta.setForeground(new Color(100, 116, 139));

        text.add(title);
        text.add(createInlineSeparator());
        text.add(subtitle);
        text.add(createInlineSeparator());
        text.add(meta);

        JPanel content = new JPanel(new BorderLayout(12, 0));
        content.setOpaque(false);
        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(34, 28));
        iconWrap.setMinimumSize(new Dimension(34, 28));
        iconWrap.setMaximumSize(new Dimension(34, 28));
        iconWrap.add(iconLabel);
        content.add(iconWrap, BorderLayout.WEST);
        content.add(text, BorderLayout.CENTER);

        JLabel status = new JLabel(safe(item.getStatus(), "-"));
        status.setOpaque(true);
        StatusPalette statusPalette = resolveStatusPalette(item.getStatus());
        status.setBackground(statusPalette.background);
        status.setForeground(statusPalette.foreground);
        status.setFont(new Font("SansSerif", Font.BOLD, 11));
        status.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        status.setPreferredSize(new Dimension(92, 34));
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setVerticalAlignment(JLabel.CENTER);

        row.add(content, BorderLayout.CENTER);
        row.add(status, BorderLayout.EAST);
        return row;
    }

    private JLabel createInlineSeparator() {
        JLabel separator = new JLabel("\u2022");
        separator.setFont(new Font("SansSerif", Font.BOLD, 12));
        separator.setForeground(new Color(148, 163, 184));
        return separator;
    }

    private JPanel buildThreeColumnSections(JPanel one, JPanel two, JPanel three) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(one);
        panel.add(two);
        panel.add(three);
        return panel;
    }

    private JPanel buildTwoColumnSections(JPanel one, JPanel two) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(one);
        panel.add(two);
        return panel;
    }

    private JPanel buildSingleSection(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private boolean isCoach() {
        String role = safe(currentUser.getRole(), "").toLowerCase();
        return role.contains("coach") || role.contains("pelatih") || role.contains("trainer") || role.contains("instruktur");
    }

    private boolean isStudent() {
        String role = safe(currentUser.getRole(), "").toLowerCase();
        return role.contains("murid") || role.contains("student") || role.contains("siswa") || role.contains("trial");
    }

    private String getRoleSubtitle() {
        if (isCoach()) {
            return "Pantau form absensi, murid dalam class, dan status kerja harian Anda.";
        }
        if (isStudent()) {
            return "Lihat kehadiran, status pembayaran, dan ringkasan class pribadi Anda.";
        }
        return "Monitor operasional akademi dari absensi, pembayaran, hingga aktivitas coach dan murid.";
    }

    private String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private MetricVisual resolveMetricVisual(String label, Color fallback) {
        String key = safe(label, "").toLowerCase();
        if (key.contains("murid")) {
            return new MetricVisual("\uD83D\uDC65", new Color(2, 132, 199));
        }
        if (key.contains("coach") || key.contains("pelatih")) {
            return new MetricVisual("\uD83D\uDC64", new Color(22, 163, 74));
        }
        if (key.contains("class") || key.contains("level")) {
            return new MetricVisual("\uD83D\uDCC5", new Color(124, 58, 237));
        }
        if (key.contains("grade")) {
            return new MetricVisual("\uD83D\uDD16", new Color(249, 115, 22));
        }
        if (key.contains("bayar") || key.contains("gaji") || key.contains("spp")) {
            return new MetricVisual("\uD83D\uDCB3", new Color(5, 150, 105));
        }
        if (key.contains("hadir") || key.contains("absensi") || key.contains("form")) {
            return new MetricVisual("\u2713", new Color(220, 38, 38));
        }
        return new MetricVisual("\u25CF", fallback);
    }

    private MetricVisual resolveSectionVisual(String title, Color fallback) {
        String key = safe(title, "").toLowerCase();
        if (key.contains("absensi") || key.contains("form")) {
            return new MetricVisual("\uD83D\uDCDD", new Color(14, 116, 144));
        }
        if (key.contains("murid")) {
            return new MetricVisual("\uD83D\uDC65", new Color(22, 163, 74));
        }
        if (key.contains("coach")) {
            return new MetricVisual("\uD83D\uDC64", new Color(249, 115, 22));
        }
        if (key.contains("pembayaran")) {
            return new MetricVisual("\uD83D\uDCB3", new Color(30, 64, 175));
        }
        if (key.contains("info") || key.contains("catatan")) {
            return new MetricVisual("\u2139", new Color(168, 85, 247));
        }
        return new MetricVisual("\u25CF", fallback);
    }

    private MetricVisual resolveRowVisual(DashboardActivityItem item, MetricVisual fallbackVisual) {
        String key = (safe(item.getTitle(), "") + " "
                + safe(item.getSubtitle(), "") + " "
                + safe(item.getStatus(), "")).toLowerCase();
        if (key.contains("lunas") || key.contains("dibayar") || key.contains("bayar")) {
            return new MetricVisual("\uD83D\uDCB3", new Color(5, 150, 105));
        }
        if (key.contains("coach") || key.contains("pelatih")) {
            return new MetricVisual("\uD83D\uDC64", new Color(249, 115, 22));
        }
        if (key.contains("murid") || key.contains("siswa")) {
            return new MetricVisual("\uD83D\uDC65", new Color(2, 132, 199));
        }
        if (key.contains("hadir") || key.contains("absensi") || key.contains("form")) {
            return new MetricVisual("\uD83D\uDCDD", new Color(30, 64, 175));
        }
        return fallbackVisual;
    }

    private JLabel createChipLabel(MetricVisual visual, int size, int fontSize) {
        JLabel label = new JLabel(visual.symbol, javax.swing.SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(visual.color);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI Symbol", Font.PLAIN, fontSize));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        label.setMaximumSize(new Dimension(size, size));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return label;
    }

    private StatusPalette resolveStatusPalette(String status) {
        String key = safe(status, "").toLowerCase();
        if (key.contains("aktif") || key.contains("lunas") || key.contains("dibayar")) {
            return new StatusPalette(new Color(220, 252, 231), new Color(21, 128, 61));
        }
        if (key.contains("belum") || key.contains("pending")) {
            return new StatusPalette(new Color(255, 237, 213), new Color(194, 65, 12));
        }
        if (key.contains("gagal") || key.contains("telat") || key.contains("overdue")) {
            return new StatusPalette(new Color(254, 226, 226), new Color(185, 28, 28));
        }
        return new StatusPalette(new Color(219, 234, 254), new Color(30, 64, 175));
    }

    private static final class MetricVisual {
        private final String symbol;
        private final Color color;

        private MetricVisual(String symbol, Color color) {
            this.symbol = symbol;
            this.color = color;
        }
    }

    private static final class StatusPalette {
        private final Color background;
        private final Color foreground;

        private StatusPalette(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }
    }

}
