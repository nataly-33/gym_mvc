package model;

import java.sql.*;

public class ExerciseModel {

    private Connection connection;

    public ExerciseModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getAll() {
        try {
            return connection.createStatement().executeQuery(
                "SELECT e.*, mg.name AS muscle_group_name " +
                "FROM exercise e JOIN muscle_group mg " +
                "ON e.id_muscle_group = mg.id_muscle_group ORDER BY e.name");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM exercise WHERE id_exercise = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByMuscleGroup(int muscleGroupId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM exercise WHERE id_muscle_group = ?");
            ps.setInt(1, muscleGroupId);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(String name, String description, String videoUrl,
                          String difficulty, int muscleGroupId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO exercise (name,description,video_url,difficulty,id_muscle_group)" +
                " VALUES (?,?,?,?,?)");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, videoUrl);
            ps.setString(4, difficulty);
            ps.setInt(5, muscleGroupId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int id, String name, String description, String videoUrl,
                          String difficulty, int muscleGroupId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE exercise SET name=?,description=?,video_url=?,difficulty=?," +
                "id_muscle_group=? WHERE id_exercise=?");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setString(3, videoUrl);
            ps.setString(4, difficulty);
            ps.setInt(5, muscleGroupId);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM exercise WHERE id_exercise = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean hasActivePlans(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT COUNT(*) FROM plan_detail WHERE id_exercise = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
