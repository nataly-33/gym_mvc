package model;
import java.sql.*;

public class ClientModel {

    Connection connection;

    public ResultSet getAll() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            return connection.createStatement().executeQuery(
                "SELECT * FROM Client ORDER BY last_name, first_name");
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public ResultSet getByCI(int ci) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM Client WHERE ci = ?");
            ps.setInt(1, ci);
            return ps.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public boolean create(int ci, String firstName, String lastName,
                          String phone, String address) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Client (ci,first_name,last_name,phone,address) VALUES (?,?,?,?,?)");
            ps.setInt(1, ci);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, phone);
            ps.setString(5, address);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(int ci, String firstName, String lastName,
                          String phone, String address) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "UPDATE Client SET first_name=?,last_name=?,phone=?,address=? WHERE ci=?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, address);
            ps.setInt(5, ci);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int ci) {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Client WHERE ci = ?");
            ps.setInt(1, ci);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
