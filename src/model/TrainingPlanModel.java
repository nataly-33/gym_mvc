package model;
import java.sql.*;

public class TrainingPlanModel {

    public ResultSet getAll() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            return connection.createStatement().executeQuery(
                "SELECT tp.*, c.first_name || ' ' || c.last_name AS client_name " +
                "FROM TrainingPlan tp JOIN Client c ON tp.ci_client = c.ci " +
                "ORDER BY tp.date DESC");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getById(int id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM TrainingPlan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByClient(int ci) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM TrainingPlan WHERE ci_client = ? ORDER BY date DESC");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public int create(String planName, String date, String objective, int ciClient) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO TrainingPlan (plan_name,date,objective,ci_client) VALUES (?,?,?,?)",
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
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE TrainingPlan SET plan_name=?,date=?,objective=? WHERE id_plan=?");
            ps.setString(1, planName);
            ps.setString(2, date);
            ps.setString(3, objective);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM TrainingPlan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
