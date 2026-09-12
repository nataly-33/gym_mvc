package model;

import java.sql.*;

public class TrainingPlanModel {

    private Connection connection;

    public TrainingPlanModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getAll() {
        try {
            return connection.createStatement().executeQuery(
                "SELECT tp.*, c.first_name || ' ' || c.last_name AS client_name " +
                "FROM training_plan tp JOIN client c ON tp.ci_client = c.ci " +
                "ORDER BY tp.date DESC");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM training_plan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByClient(int ci) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM training_plan WHERE ci_client = ? ORDER BY date DESC");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // Retorna el id_plan generado, o -1 si falla
    public int create(String planName, String date, String objective, int ciClient) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO training_plan (plan_name,date,objective,ci_client) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, planName);
            ps.setString(2, date);
            ps.setString(3, objective);
            ps.setInt(4, ciClient);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        } catch (SQLException e) { e.printStackTrace(); return -1; }
    }

    public boolean update(int id, String planName, String date, String objective) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE training_plan SET plan_name=?,date=?,objective=? WHERE id_plan=?");
            ps.setString(1, planName);
            ps.setString(2, date);
            ps.setString(3, objective);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM training_plan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
