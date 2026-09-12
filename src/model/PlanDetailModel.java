package model;

import java.sql.*;

public class PlanDetailModel {

    private Connection connection;

    public PlanDetailModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getByPlan(int planId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT pd.*, e.name AS exercise_name, e.video_url " +
                "FROM plan_detail pd JOIN exercise e ON pd.id_exercise = e.id_exercise " +
                "WHERE pd.id_plan = ? ORDER BY pd.exercise_order");
            ps.setInt(1, planId);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // Calcula el siguiente id_detail secuencial DENTRO del plan dado
    public int getNextDetailId(int planId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT COALESCE(MAX(id_detail), 0) + 1 FROM plan_detail WHERE id_plan = ?");
            ps.setInt(1, planId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) { e.printStackTrace(); return 1; }
    }

    // id_detail debe calcularse con getNextDetailId() antes de llamar a este metodo
    public boolean create(int planId, int detailId, int exerciseId,
                          int sets, int reps, int restTime, int order) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO plan_detail " +
                "(id_plan,id_detail,id_exercise,sets,reps,rest_time,exercise_order)" +
                " VALUES (?,?,?,?,?,?,?)");
            ps.setInt(1, planId);
            ps.setInt(2, detailId);
            ps.setInt(3, exerciseId);
            ps.setInt(4, sets);
            ps.setInt(5, reps);
            ps.setInt(6, restTime);
            ps.setInt(7, order);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Ambas partes del PK son necesarias para identificar la fila
    public boolean update(int planId, int detailId, int sets, int reps, int restTime) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE plan_detail SET sets=?,reps=?,rest_time=? " +
                "WHERE id_plan=? AND id_detail=?");
            ps.setInt(1, sets);
            ps.setInt(2, reps);
            ps.setInt(3, restTime);
            ps.setInt(4, planId);
            ps.setInt(5, detailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int planId, int detailId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM plan_detail WHERE id_plan=? AND id_detail=?");
            ps.setInt(1, planId);
            ps.setInt(2, detailId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteByPlan(int planId) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM plan_detail WHERE id_plan = ?");
            ps.setInt(1, planId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
