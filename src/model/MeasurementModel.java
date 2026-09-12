package model;

import java.sql.*;

public class MeasurementModel {

    private Connection connection;

    public MeasurementModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getAll() {
        try {
            return connection.createStatement().executeQuery(
                "SELECT m.*, c.first_name || ' ' || c.last_name AS client_name " +
                "FROM measurement m JOIN client c ON m.ci_client = c.ci " +
                "ORDER BY m.measurement_date DESC");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByClient(int ci) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM measurement WHERE ci_client = ? ORDER BY measurement_date DESC");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM measurement WHERE id_measurement = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(int ci, String date, double weightKg, double bodyFat,
                          double chest, double waist, double hip, String notes) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO measurement " +
                "(ci_client,measurement_date,weight_kg,body_fat_pct,chest_cm,waist_cm,hip_cm,notes)" +
                " VALUES (?,?,?,?,?,?,?,?)");
            ps.setInt(1, ci);
            ps.setString(2, date);
            ps.setDouble(3, weightKg);
            ps.setDouble(4, bodyFat);
            ps.setDouble(5, chest);
            ps.setDouble(6, waist);
            ps.setDouble(7, hip);
            ps.setString(8, notes);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int id, String date, double weightKg, double bodyFat,
                          double chest, double waist, double hip, String notes) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE measurement SET measurement_date=?,weight_kg=?,body_fat_pct=?," +
                "chest_cm=?,waist_cm=?,hip_cm=?,notes=? WHERE id_measurement=?");
            ps.setString(1, date);
            ps.setDouble(2, weightKg);
            ps.setDouble(3, bodyFat);
            ps.setDouble(4, chest);
            ps.setDouble(5, waist);
            ps.setDouble(6, hip);
            ps.setString(7, notes);
            ps.setInt(8, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM measurement WHERE id_measurement = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
