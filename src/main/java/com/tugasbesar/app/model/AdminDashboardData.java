package com.tugasbesar.app.model;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardData {
    private List<DashboardMetric> metrics = new ArrayList<>();
    private List<DashboardActivityItem> recentAttendanceForms = new ArrayList<>();
    private List<DashboardActivityItem> recentStudentPayments = new ArrayList<>();
    private List<DashboardActivityItem> recentCoachPayments = new ArrayList<>();

    public List<DashboardMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<DashboardMetric> metrics) {
        this.metrics = metrics;
    }

    public List<DashboardActivityItem> getRecentAttendanceForms() {
        return recentAttendanceForms;
    }

    public void setRecentAttendanceForms(List<DashboardActivityItem> recentAttendanceForms) {
        this.recentAttendanceForms = recentAttendanceForms;
    }

    public List<DashboardActivityItem> getRecentStudentPayments() {
        return recentStudentPayments;
    }

    public void setRecentStudentPayments(List<DashboardActivityItem> recentStudentPayments) {
        this.recentStudentPayments = recentStudentPayments;
    }

    public List<DashboardActivityItem> getRecentCoachPayments() {
        return recentCoachPayments;
    }

    public void setRecentCoachPayments(List<DashboardActivityItem> recentCoachPayments) {
        this.recentCoachPayments = recentCoachPayments;
    }
}
