package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TrainingPlanModel {

    private PlanDetailModel detailModel;
    private Connection connection;

    public TrainingPlanModel() {
        this.detailModel = new PlanDetailModel();
    }

    public PlanDetailModel getDetailModel() {
        return detailModel;
    }

    public boolean cambiarEstadoEnBD(int planId, String estadoNombre) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE TrainingPlan SET status = ? WHERE id_plan = ?");
            ps.setString(1, estadoNombre);
            ps.setInt(2, planId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getAll() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            return connection.createStatement().executeQuery(
                "SELECT tp.*, c.first_name || ' ' || c.last_name AS client_name " +
                "FROM TrainingPlan tp JOIN Client c ON tp.ci_client = c.ci " +
                "ORDER BY tp.date DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getByClient(int ci) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM TrainingPlan WHERE ci_client = ? ORDER BY date DESC");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getById(int id) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM TrainingPlan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int create(String planName, String date, String objective, int ciClient) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO TrainingPlan (plan_name,date,objective,ci_client,status) VALUES (?,?,?,?,'Borrador')",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, planName);
            ps.setString(2, date);
            ps.setString(3, objective);
            ps.setInt(4, ciClient);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean update(int id, String planName, String date, String objective) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE TrainingPlan SET plan_name=?,date=?,objective=? WHERE id_plan=?");
            ps.setString(1, planName);
            ps.setString(2, date);
            ps.setString(3, objective);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            detailModel.deleteByPlan(id);
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM TrainingPlan WHERE id_plan = ?");
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveWithDetails(String planName, String date, String objective, int ciClient, int[][] details) {
        int planId = create(planName, date, objective, ciClient);
        if (planId == -1) return false;
        for (int[] d : details) {
            int nextId = detailModel.getNextDetailId(planId);
            detailModel.create(planId, nextId, d[0], d[1], d[2], d[3], d[4]);
        }
        return true;
    }

    public boolean updateWithDetails(int planId, String planName, String date, String objective, int[][] details) {
        boolean ok = update(planId, planName, date, objective);
        if (!ok) return false;
        detailModel.deleteByPlan(planId);
        for (int[] d : details) {
            int nextId = detailModel.getNextDetailId(planId);
            detailModel.create(planId, nextId, d[0], d[1], d[2], d[3], d[4]);
        }
        return true;
    }

    public ResultSet getDetailsByPlan(int planId) {
        return detailModel.getByPlan(planId);
    }
}
