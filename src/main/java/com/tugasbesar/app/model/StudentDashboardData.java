package com.tugasbesar.app.model;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardData {
    private List<DashboardMetric> metrics = new ArrayList<>();
    private List<DashboardActivityItem> recentAttendance = new ArrayList<>();
    private List<DashboardActivityItem> recentPayments = new ArrayList<>();
    private List<DashboardActivityItem> quickNotes = new ArrayList<>();

    public List<DashboardMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<DashboardMetric> metrics) {
        this.metrics = metrics;
    }

    public List<DashboardActivityItem> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<DashboardActivityItem> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }

    public List<DashboardActivityItem> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<DashboardActivityItem> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<DashboardActivityItem> getQuickNotes() {
        return quickNotes;
    }

    public void setQuickNotes(List<DashboardActivityItem> quickNotes) {
        this.quickNotes = quickNotes;
    }
}
