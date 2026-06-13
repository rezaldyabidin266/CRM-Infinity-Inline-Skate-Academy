package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.ProgressAssessment;
import com.tugasbesar.app.model.ProgressTemplate;
import com.tugasbesar.app.model.ProgressTemplateItem;
import com.tugasbesar.app.model.StudentProgressItem;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProgressRepository {
    public List<ProgressTemplate> findAllTemplates() {
        String sql = "SELECT pt.uuid, pt.level_uuid, l.name AS level_name, pt.name, pt.notes, pt.is_active, "
                + "COUNT(pti.uuid) AS item_count "
                + "FROM progress_templates pt "
                + "JOIN levels l ON l.uuid = pt.level_uuid "
                + "LEFT JOIN progress_template_items pti ON pti.template_uuid = pt.uuid "
                + "GROUP BY pt.uuid, pt.level_uuid, l.name, pt.name, pt.notes, pt.is_active "
                + "ORDER BY l.sort_order ASC, pt.name ASC";
        List<ProgressTemplate> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(mapTemplate(resultSet));
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil template progress.", exception);
        }
    }

    public List<ProgressTemplate> findActiveTemplatesByLevel(String levelUuid) {
        String sql = "SELECT pt.uuid, pt.level_uuid, l.name AS level_name, pt.name, pt.notes, pt.is_active, "
                + "(SELECT COUNT(*) FROM progress_template_items i WHERE i.template_uuid = pt.uuid AND i.is_active = 1) AS item_count "
                + "FROM progress_templates pt "
                + "JOIN levels l ON l.uuid = pt.level_uuid "
                + "WHERE pt.is_active = 1 AND pt.level_uuid = ? "
                + "ORDER BY pt.name ASC";
        List<ProgressTemplate> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, levelUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapTemplate(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil template progress level.", exception);
        }
    }

    public List<ProgressTemplate> findActiveTemplates() {
        String sql = "SELECT pt.uuid, pt.level_uuid, l.name AS level_name, pt.name, pt.notes, pt.is_active, "
                + "(SELECT COUNT(*) FROM progress_template_items i WHERE i.template_uuid = pt.uuid AND i.is_active = 1) AS item_count "
                + "FROM progress_templates pt "
                + "JOIN levels l ON l.uuid = pt.level_uuid "
                + "WHERE pt.is_active = 1 "
                + "ORDER BY l.sort_order ASC, pt.name ASC";
        List<ProgressTemplate> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rows.add(mapTemplate(resultSet));
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil template progress aktif.", exception);
        }
    }

    public List<ProgressTemplate> findActiveTemplatesByCoachGrade(String coachUuid) {
        String sql = "SELECT pt.uuid, pt.level_uuid, l.name AS level_name, pt.name, pt.notes, pt.is_active, "
                + "(SELECT COUNT(*) FROM progress_template_items i WHERE i.template_uuid = pt.uuid AND i.is_active = 1) AS item_count "
                + "FROM users coach "
                + "LEFT JOIN levels coach_level ON coach_level.uuid = coach.level_uuid "
                + "LEFT JOIN grades coach_grade ON coach_grade.uuid = coach.grade_uuid "
                + "LEFT JOIN grades coach_level_grade ON coach_level_grade.uuid = coach_level.grade_uuid "
                + "JOIN progress_templates pt ON pt.is_active = 1 "
                + "JOIN levels l ON l.uuid = pt.level_uuid "
                + "JOIN grades template_grade ON template_grade.uuid = l.grade_uuid "
                + "WHERE coach.uuid = ? "
                + "AND ("
                + "l.grade_uuid = COALESCE(coach_level.grade_uuid, coach.grade_uuid) "
                + "OR template_grade.grade_value = COALESCE(coach_level_grade.grade_value, coach_grade.grade_value) "
                + "OR LOWER(template_grade.name) = LOWER(COALESCE(coach_level_grade.name, coach_grade.name))"
                + ") "
                + "ORDER BY l.sort_order ASC, pt.name ASC";
        List<ProgressTemplate> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, coachUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapTemplate(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil template progress grade coach.", exception);
        }
    }

    public ProgressTemplate createTemplate(ProgressTemplate template) {
        String uuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO progress_templates (uuid, level_uuid, name, notes, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, template.getLevelUuid());
            statement.setString(3, template.getName());
            statement.setString(4, template.getNotes());
            statement.setBoolean(5, template.isActive());
            statement.executeUpdate();
            template.setUuid(uuid);
            return template;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan template progress.", exception);
        }
    }

    public void updateTemplate(ProgressTemplate template) {
        String sql = "UPDATE progress_templates SET level_uuid = ?, name = ?, notes = ?, is_active = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, template.getLevelUuid());
            statement.setString(2, template.getName());
            statement.setString(3, template.getNotes());
            statement.setBoolean(4, template.isActive());
            statement.setString(5, template.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui template progress.", exception);
        }
    }

    public void deleteTemplate(String templateUuid) {
        String sql = "DELETE FROM progress_templates WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, templateUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus template progress.", exception);
        }
    }

    public List<ProgressTemplateItem> findItemsByTemplate(String templateUuid, boolean activeOnly) {
        String sql = "SELECT uuid, template_uuid, kode_unit, kompetensi, category, is_active, sort_order "
                + "FROM progress_template_items WHERE template_uuid = ? ";
        if (activeOnly) {
            sql += "AND is_active = 1 ";
        }
        sql += "ORDER BY sort_order ASC, kode_unit ASC";
        List<ProgressTemplateItem> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, templateUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(mapItem(resultSet));
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil item progress.", exception);
        }
    }

    public ProgressTemplateItem createItem(ProgressTemplateItem item) {
        String uuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO progress_template_items (uuid, template_uuid, kode_unit, kompetensi, category, is_active, sort_order) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, item.getTemplateUuid());
            statement.setString(3, item.getKodeUnit());
            statement.setString(4, item.getKompetensi());
            statement.setString(5, item.getCategory());
            statement.setBoolean(6, item.isActive());
            statement.setInt(7, item.getSortOrder());
            statement.executeUpdate();
            item.setUuid(uuid);
            return item;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan item progress.", exception);
        }
    }

    public void updateItem(ProgressTemplateItem item) {
        String sql = "UPDATE progress_template_items SET kode_unit = ?, kompetensi = ?, category = ?, is_active = ?, sort_order = ? WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getKodeUnit());
            statement.setString(2, item.getKompetensi());
            statement.setString(3, item.getCategory());
            statement.setBoolean(4, item.isActive());
            statement.setInt(5, item.getSortOrder());
            statement.setString(6, item.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui item progress.", exception);
        }
    }

    public void deleteItem(String itemUuid) {
        String sql = "DELETE FROM progress_template_items WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, itemUuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus item progress.", exception);
        }
    }

    public List<StudentProgressItem> findStudentChecklist(String templateUuid, String muridUuid, String assessmentUuid) {
        String sql = "SELECT spr.uuid AS record_uuid, pti.uuid AS item_uuid, u.uuid AS murid_uuid, u.full_name AS murid_name, "
                + "coach.full_name AS coach_name, l.name AS level_name, pt.name AS template_name, "
                + "spa.uuid AS assessment_uuid, spa.name AS assessment_name, spa.assessment_date, "
                + "pti.kode_unit, pti.kompetensi, pti.category, COALESCE(spr.is_passed, 0) AS is_passed, "
                + "spr.checked_at, COALESCE(spr.notes, '') AS notes "
                + "FROM progress_template_items pti "
                + "JOIN progress_templates pt ON pt.uuid = pti.template_uuid "
                + "JOIN users u ON u.uuid = ? "
                + "LEFT JOIN levels l ON l.uuid = u.level_uuid "
                + "LEFT JOIN student_progress_assessments spa ON spa.uuid = ? "
                + "LEFT JOIN users coach ON coach.uuid = spa.coach_uuid "
                + "LEFT JOIN student_progress_records spr ON spr.item_uuid = pti.uuid AND spr.murid_uuid = u.uuid AND spr.assessment_uuid = spa.uuid "
                + "WHERE pti.template_uuid = ? AND pti.is_active = 1 "
                + "ORDER BY pti.sort_order ASC, pti.kode_unit ASC";
        List<StudentProgressItem> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, muridUuid);
            statement.setString(2, assessmentUuid);
            statement.setString(3, templateUuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    StudentProgressItem row = new StudentProgressItem();
                    row.setRecordUuid(resultSet.getString("record_uuid"));
                    row.setItemUuid(resultSet.getString("item_uuid"));
                    row.setMuridUuid(resultSet.getString("murid_uuid"));
                    row.setMuridName(resultSet.getString("murid_name"));
                    row.setCoachName(resultSet.getString("coach_name"));
                    row.setLevelName(resultSet.getString("level_name"));
                    row.setTemplateName(resultSet.getString("template_name"));
                    row.setAssessmentUuid(resultSet.getString("assessment_uuid"));
                    row.setAssessmentName(resultSet.getString("assessment_name"));
                    Date assessmentDate = resultSet.getDate("assessment_date");
                    row.setAssessmentDate(assessmentDate == null ? "-" : assessmentDate.toString());
                    row.setKodeUnit(resultSet.getString("kode_unit"));
                    row.setKompetensi(resultSet.getString("kompetensi"));
                    row.setCategory(resultSet.getString("category"));
                    row.setPassed(resultSet.getBoolean("is_passed"));
                    Timestamp checkedAt = resultSet.getTimestamp("checked_at");
                    row.setCheckedAt(checkedAt == null ? "-" : checkedAt.toString());
                    row.setNotes(resultSet.getString("notes"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil checklist progress murid.", exception);
        }
    }

    public List<ProgressAssessment> findAssessmentsByStudent(String muridUuid, String templateUuid) {
        String sql = "SELECT spa.uuid, spa.murid_uuid, spa.coach_uuid, spa.template_uuid, spa.name, spa.assessment_date, spa.notes, "
                + "u.full_name AS murid_name, coach.full_name AS coach_name, l.name AS level_name, pt.name AS template_name, "
                + "COUNT(pti.uuid) AS total_items, "
                + "SUM(CASE WHEN COALESCE(spr.is_passed, 0) = 1 THEN 1 ELSE 0 END) AS passed_items "
                + "FROM student_progress_assessments spa "
                + "JOIN users u ON u.uuid = spa.murid_uuid "
                + "LEFT JOIN users coach ON coach.uuid = spa.coach_uuid "
                + "LEFT JOIN levels l ON l.uuid = u.level_uuid "
                + "JOIN progress_templates pt ON pt.uuid = spa.template_uuid "
                + "LEFT JOIN progress_template_items pti ON pti.template_uuid = pt.uuid AND pti.is_active = 1 "
                + "LEFT JOIN student_progress_records spr ON spr.assessment_uuid = spa.uuid AND spr.item_uuid = pti.uuid "
                + "WHERE spa.murid_uuid = ? ";
        if (templateUuid != null && !templateUuid.trim().isEmpty()) {
            sql += "AND spa.template_uuid = ? ";
        }
        sql += "GROUP BY spa.uuid, spa.murid_uuid, spa.coach_uuid, spa.template_uuid, spa.name, spa.assessment_date, spa.notes, "
                + "u.full_name, coach.full_name, l.name, pt.name "
                + "ORDER BY spa.assessment_date DESC, spa.created_at DESC";
        List<ProgressAssessment> rows = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, muridUuid);
            if (templateUuid != null && !templateUuid.trim().isEmpty()) {
                statement.setString(2, templateUuid);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ProgressAssessment row = new ProgressAssessment();
                    row.setUuid(resultSet.getString("uuid"));
                    row.setMuridUuid(resultSet.getString("murid_uuid"));
                    row.setCoachUuid(resultSet.getString("coach_uuid"));
                    row.setTemplateUuid(resultSet.getString("template_uuid"));
                    row.setAssessmentName(resultSet.getString("name"));
                    Date assessmentDate = resultSet.getDate("assessment_date");
                    row.setAssessmentDate(assessmentDate == null ? "-" : assessmentDate.toString());
                    row.setNotes(resultSet.getString("notes"));
                    row.setMuridName(resultSet.getString("murid_name"));
                    row.setCoachName(resultSet.getString("coach_name"));
                    row.setLevelName(resultSet.getString("level_name"));
                    row.setTemplateName(resultSet.getString("template_name"));
                    row.setTotalItems(resultSet.getInt("total_items"));
                    row.setPassedItems(resultSet.getInt("passed_items"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil riwayat progress murid.", exception);
        }
    }

    public ProgressAssessment createAssessment(String coachUuid, String muridUuid, String templateUuid, String assessmentName, String assessmentDate, String notes) {
        String uuid = UUID.randomUUID().toString();
        String sql = "INSERT INTO student_progress_assessments (uuid, murid_uuid, coach_uuid, template_uuid, name, assessment_date, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        ProgressAssessment assessment = new ProgressAssessment();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, muridUuid);
            statement.setString(3, coachUuid);
            statement.setString(4, templateUuid);
            statement.setString(5, assessmentName);
            statement.setDate(6, Date.valueOf(assessmentDate));
            statement.setString(7, notes == null ? "" : notes);
            statement.executeUpdate();
            assessment.setUuid(uuid);
            assessment.setMuridUuid(muridUuid);
            assessment.setCoachUuid(coachUuid);
            assessment.setTemplateUuid(templateUuid);
            assessment.setAssessmentName(assessmentName);
            assessment.setAssessmentDate(assessmentDate);
            assessment.setNotes(notes == null ? "" : notes);
            return assessment;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal membuat riwayat progress.", exception);
        }
    }

    public void saveStudentChecklist(String coachUuid, String muridUuid, String templateUuid, String assessmentUuid, List<StudentProgressItem> items) {
        String sql = "INSERT INTO student_progress_records "
                + "(uuid, assessment_uuid, murid_uuid, coach_uuid, template_uuid, item_uuid, is_passed, checked_at, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, CASE WHEN ? = 1 THEN CURRENT_TIMESTAMP ELSE NULL END, ?) "
                + "ON DUPLICATE KEY UPDATE coach_uuid = VALUES(coach_uuid), template_uuid = VALUES(template_uuid), "
                + "is_passed = VALUES(is_passed), checked_at = CASE WHEN VALUES(is_passed) = 1 THEN CURRENT_TIMESTAMP ELSE NULL END, "
                + "notes = VALUES(notes)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (StudentProgressItem item : items) {
                statement.setString(1, UUID.randomUUID().toString());
                statement.setString(2, assessmentUuid);
                statement.setString(3, muridUuid);
                statement.setString(4, coachUuid);
                statement.setString(5, templateUuid);
                statement.setString(6, item.getItemUuid());
                statement.setBoolean(7, item.isPassed());
                statement.setBoolean(8, item.isPassed());
                statement.setString(9, item.getNotes() == null ? "" : item.getNotes());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan checklist progress.", exception);
        }
    }

    private ProgressTemplate mapTemplate(ResultSet resultSet) throws SQLException {
        ProgressTemplate template = new ProgressTemplate();
        template.setUuid(resultSet.getString("uuid"));
        template.setLevelUuid(resultSet.getString("level_uuid"));
        template.setLevelName(resultSet.getString("level_name"));
        template.setName(resultSet.getString("name"));
        template.setNotes(resultSet.getString("notes"));
        template.setActive(resultSet.getBoolean("is_active"));
        template.setItemCount(resultSet.getInt("item_count"));
        return template;
    }

    private ProgressTemplateItem mapItem(ResultSet resultSet) throws SQLException {
        ProgressTemplateItem item = new ProgressTemplateItem();
        item.setUuid(resultSet.getString("uuid"));
        item.setTemplateUuid(resultSet.getString("template_uuid"));
        item.setKodeUnit(resultSet.getString("kode_unit"));
        item.setKompetensi(resultSet.getString("kompetensi"));
        item.setCategory(resultSet.getString("category"));
        item.setActive(resultSet.getBoolean("is_active"));
        item.setSortOrder(resultSet.getInt("sort_order"));
        return item;
    }
}
