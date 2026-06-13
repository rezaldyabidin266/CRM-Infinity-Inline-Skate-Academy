package com.tugasbesar.app.model;

public class DashboardMetric {
    private String label;
    private String value;

    public DashboardMetric() {
    }

    public DashboardMetric(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
