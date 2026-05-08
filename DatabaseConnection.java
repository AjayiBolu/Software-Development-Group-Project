package com.companyz.ems.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private final Connection conn;

    private DatabaseConnection() throws SQLException, IOException {
        Properties props = loadProperties();
        String url  = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");

        if (url == null || user == null || pass == null) {
            throw new IOException("db.properties is missing db.url, db.user, or db.password");
        }

        this.conn = DriverManager.getConnection(url, user, pass);
    }

    private Properties loadProperties() throws IOException {
        Properties props = new Properties();

        String[] paths = {
            "db.properties",
            "resources/db.properties",
            "src/resources/db.properties"
        };
        IOException lastErr = null;
        for (String path : paths) {
            try (InputStream in = new FileInputStream(path)) {
                props.load(in);
                return props;
            } catch (IOException ex) {
                lastErr = ex;
            }
        }

        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                return props;
            }
        }
        throw new IOException("Could not locate db.properties", lastErr);
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            try {
                instance = new DatabaseConnection();
            } catch (Exception ex) {
                throw new RuntimeException("Could not connect to database: " + ex.getMessage(), ex);
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {  }
    }
}
