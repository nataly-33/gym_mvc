package model;
import java.sql.*;

public class MeasurementModel {

    // Orden canónico: weight, body_fat, chest, glutes, waist, date (igual que el schema)

    public ResultSet getAll() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            return connection.createStatement().executeQuery(
                "SELECT m.*, c.first_name || ' ' || c.last_name AS client_name " +
                "FROM Measurement m JOIN Client c ON m.ci_client = c.ci " +
                "ORDER BY m.date DESC");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByClient(int ci) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Measurement WHERE ci_client = ? ORDER BY date DESC");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Measurement WHERE id_measurement = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // Firma: (ci, weight, body_fat, chest, glutes, waist, date)
    public boolean create(int ci, double weight, double bodyFat,
                          double chest, double glutes, double waist, String date) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Measurement " +
                "(ci_client, weight, body_fat, chest, glutes, waist, date)" +
                " VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, ci);
            ps.setDouble(2, weight);
            ps.setDouble(3, bodyFat);
            ps.setDouble(4, chest);
            ps.setDouble(5, glutes);
            ps.setDouble(6, waist);
            ps.setString(7, date);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Firma: (id, weight, body_fat, chest, glutes, waist, date)
    public boolean update(int id, double weight, double bodyFat,
                          double chest, double glutes, double waist, String date) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE Measurement SET weight=?, body_fat=?, chest=?, " +
                "glutes=?, waist=?, date=? WHERE id_measurement=?");
            ps.setDouble(1, weight);
            ps.setDouble(2, bodyFat);
            ps.setDouble(3, chest);
            ps.setDouble(4, glutes);
            ps.setDouble(5, waist);
            ps.setString(6, date);
            ps.setInt(7, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Measurement WHERE id_measurement = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
