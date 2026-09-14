package model;
import java.sql.*;

public class MuscleGroupModel {

    Connection connection;

    public ResultSet getAll() {
        try {
             this.connection = DatabaseConnection.getInstance().getConnection();
            return connection.createStatement().executeQuery(
                "SELECT * FROM MuscleGroup ORDER BY id_muscle_group ASC");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
             this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM MuscleGroup WHERE id_muscle_group = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(String name, String description) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO MuscleGroup (name, description) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int id, String name, String description) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE MuscleGroup SET name=?,description=? WHERE id_muscle_group=?");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM MuscleGroup WHERE id_muscle_group = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
