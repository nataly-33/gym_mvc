package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_PATH = "gym.db";

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            initSchema();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get("database/schema.sql")));
        Statement stmt = connection.createStatement();
        for (String query : sql.split(";")) {
            if (!query.trim().isEmpty()) {
                stmt.execute(query.trim());
            }
        }
    }
}
