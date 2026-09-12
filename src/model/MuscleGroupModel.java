package model;

import java.sql.*;

public class MuscleGroupModel {

    private Connection connection;

    public MuscleGroupModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getAll() {
        try {
            return connection.createStatement().executeQuery(
                "SELECT * FROM muscle_group ORDER BY name");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM muscle_group WHERE id_muscle_group = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(String name, String description) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO muscle_group (name, description) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int id, String name, String description) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE muscle_group SET name=?, description=? WHERE id_muscle_group=?");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM muscle_group WHERE id_muscle_group = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
