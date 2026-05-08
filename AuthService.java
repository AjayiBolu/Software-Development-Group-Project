package com.companyz.ems.service;

import com.companyz.ems.db.DatabaseConnection;
import com.companyz.ems.user.GeneralEmployee;
import com.companyz.ems.user.HRAdmin;
import com.companyz.ems.user.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    private final Connection conn = DatabaseConnection.getInstance().getConnection();

    public User authenticate(String username, String password) {
        String sql = "SELECT userID, username, password_hash, role, empid FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String storedHash = rs.getString("password_hash");
                if (!storedHash.equalsIgnoreCase(hash(password))) return null;

                int userID = rs.getInt("userID");
                String role = rs.getString("role");
                if ("HR_ADMIN".equals(role)) {
                    return new HRAdmin(userID, rs.getString("username"));
                } else {
                    int empID = rs.getInt("empid");
                    return new GeneralEmployee(userID, rs.getString("username"), empID);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("authenticate failed: " + ex.getMessage(), ex);
        }
    }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException ex) {
            throw new RuntimeException("hash failed: " + ex.getMessage(), ex);
        }
    }
}
