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
        setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));

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

        JLabel badge = new JLabel(getRoleBadge());
        badge.setOpaque(true);
        badge.setBackground(new Color(30, 64, 175));
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 13));
        badge.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        hero.add(text, BorderLayout.CENTER);
        hero.add(badge, BorderLayout.EAST);
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
            add(Box.createVerticalStrut(18));
            add(buildSingleSection(buildActivityCard("Catatan Cepat", "Ringkasan akses dan fokus kerja Anda.", data.getQuickNotes(), ACCENT_THREE)));
            return;
        }

        if (isStudent()) {
            StudentDashboardData data = dashboardRepository.loadStudentDashboardData(currentUser);
            add(buildMetricsGrid(data.getMetrics(), 3));
            add(Box.createVerticalStrut(18));
            add(buildTwoColumnSections(
                    buildActivityCard("Absensi Saya", "Riwayat hadir terbaru Anda.", data.getRecentAttendance(), ACCENT_FOUR),
                    buildActivityCard("Pembayaran Saya", "Status pembayaran pribadi terbaru.", data.getRecentPayments(), ACCENT_TWO)));
            add(Box.createVerticalStrut(18));
            add(buildSingleSection(buildActivityCard("Info Siswa", "Informasi penting untuk akun murid.", data.getQuickNotes(), ACCENT_THREE)));
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rows * 118));

        Color[] accents = new Color[]{ACCENT_ONE, ACCENT_TWO, ACCENT_THREE, ACCENT_FOUR};
        for (int i = 0; i < metrics.size(); i++) {
            panel.add(buildMetricCard(metrics.get(i), accents[i % accents.length]));
        }
        return panel;
    }

    private JPanel buildMetricCard(DashboardMetric metric, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel line = new JPanel();
        line.setBackground(accent);
        line.setPreferredSize(new Dimension(12, 12));
        line.setMaximumSize(new Dimension(12, 12));

        JLabel label = new JLabel(metric.getLabel());
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(SUBTITLE_COLOR);

        JLabel value = new JLabel(metric.getValue());
        value.setFont(new Font("SansSerif", Font.BOLD, 28));
        value.setForeground(TITLE_COLOR);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(line);
        body.add(Box.createVerticalStrut(12));
        body.add(label);
        body.add(Box.createVerticalStrut(10));
        body.add(value);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityCard(String title, String subtitle, List<DashboardActivityItem> items, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(SUBTITLE_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(54, 4));
        accentBar.setMaximumSize(new Dimension(54, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(accentBar);
        header.add(Box.createVerticalStrut(10));
        header.add(titleLabel);
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
                list.add(buildActivityRow(item));
                list.add(Box.createVerticalStrut(10));
            }
        }

        card.add(header, BorderLayout.NORTH);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildActivityRow(DashboardActivityItem item) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(safe(item.getTitle(), "-"));
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TITLE_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(safe(item.getSubtitle(), "-"));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(SUBTITLE_COLOR);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel meta = new JLabel(safe(item.getMeta(), "-"));
        meta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        meta.setForeground(new Color(100, 116, 139));
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(title);
        text.add(Box.createVerticalStrut(2));
        text.add(subtitle);
        text.add(Box.createVerticalStrut(4));
        text.add(meta);

        JLabel status = new JLabel(safe(item.getStatus(), "-"));
        status.setOpaque(true);
        status.setBackground(new Color(239, 246, 255));
        status.setForeground(new Color(30, 64, 175));
        status.setFont(new Font("SansSerif", Font.BOLD, 11));
        status.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        row.add(text, BorderLayout.CENTER);
        row.add(status, BorderLayout.EAST);
        return row;
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

    private String getRoleBadge() {
        if (currentUser.isSuperAdmin()) {
            return "SUPER ADMIN";
        }
        if (isCoach()) {
            return "COACH DASHBOARD";
        }
        if (isStudent()) {
            return "MURID DASHBOARD";
        }
        return "ADMIN DASHBOARD";
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
}
