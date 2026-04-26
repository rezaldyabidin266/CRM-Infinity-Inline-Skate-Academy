package com.tugasbesar.app.repository;

import com.tugasbesar.app.database.DatabaseConnection;
import com.tugasbesar.app.model.Equipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EquipmentRepository {
    public List<Equipment> findAll() {
        String sql = "SELECT uuid, nama_peralatan, jenis, ukuran, jumlah, kondisi, status, keterangan, updated_at "
                + "FROM master_peralatan ORDER BY nama_peralatan ASC";
        List<Equipment> items = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                items.add(mapEquipment(resultSet));
            }
            return items;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data peralatan.", exception);
        }
    }

    public Equipment findByName(String name) {
        String sql = "SELECT uuid, nama_peralatan, jenis, ukuran, jumlah, kondisi, status, keterangan, updated_at "
                + "FROM master_peralatan WHERE LOWER(nama_peralatan) = LOWER(?) LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEquipment(resultSet);
                }
            }
            return null;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal mengambil data peralatan.", exception);
        }
    }

    public Equipment create(Equipment equipment) {
        String sql = "INSERT INTO master_peralatan "
                + "(uuid, nama_peralatan, jenis, ukuran, jumlah, kondisi, status, keterangan) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String uuid = UUID.randomUUID().toString();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, equipment.getName());
            statement.setString(3, equipment.getType());
            statement.setString(4, equipment.getSize());
            statement.setInt(5, equipment.getQuantity());
            statement.setString(6, equipment.getCondition());
            statement.setString(7, equipment.getStatus());
            statement.setString(8, equipment.getNotes());
            statement.executeUpdate();
            equipment.setUuid(uuid);
            return equipment;
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menyimpan peralatan: " + exception.getMessage(), exception);
        }
    }

    public void update(Equipment equipment) {
        String sql = "UPDATE master_peralatan SET nama_peralatan = ?, jenis = ?, ukuran = ?, jumlah = ?, kondisi = ?, status = ?, keterangan = ? "
                + "WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, equipment.getName());
            statement.setString(2, equipment.getType());
            statement.setString(3, equipment.getSize());
            statement.setInt(4, equipment.getQuantity());
            statement.setString(5, equipment.getCondition());
            statement.setString(6, equipment.getStatus());
            statement.setString(7, equipment.getNotes());
            statement.setString(8, equipment.getUuid());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal memperbarui peralatan.", exception);
        }
    }

    public void deleteByUuid(String uuid) {
        String sql = "DELETE FROM master_peralatan WHERE uuid = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Gagal menghapus peralatan.", exception);
        }
    }

    private Equipment mapEquipment(ResultSet resultSet) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setUuid(resultSet.getString("uuid"));
        equipment.setName(resultSet.getString("nama_peralatan"));
        equipment.setType(resultSet.getString("jenis"));
        equipment.setSize(resultSet.getString("ukuran"));
        equipment.setQuantity(resultSet.getInt("jumlah"));
        equipment.setCondition(resultSet.getString("kondisi"));
        equipment.setStatus(resultSet.getString("status"));
        equipment.setNotes(resultSet.getString("keterangan"));
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");
        if (updatedAt != null) {
            equipment.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return equipment;
    }
}
