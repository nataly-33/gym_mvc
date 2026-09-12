package model;

import java.sql.*;

public class ClientModel {

    private Connection connection;

    public ClientModel() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public ResultSet getAll() {
        try {
            return connection.createStatement().executeQuery(
                "SELECT * FROM client ORDER BY last_name, first_name");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByCI(int ci) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM client WHERE ci = ?");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(int ci, String firstName, String lastName, int age,
                          String phone, String address, double weight, double height) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO client (ci,first_name,last_name,age,phone,address,weight,height)" +
                " VALUES (?,?,?,?,?,?,?,?)");
            ps.setInt(1, ci);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setInt(4, age);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setDouble(7, weight);
            ps.setDouble(8, height);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int ci, String firstName, String lastName, int age,
                          String phone, String address, double weight, double height) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE client SET first_name=?,last_name=?,age=?,phone=?," +
                "address=?,weight=?,height=? WHERE ci=?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, age);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setDouble(6, weight);
            ps.setDouble(7, height);
            ps.setInt(8, ci);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int ci) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM client WHERE ci = ?");
            ps.setInt(1, ci);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
