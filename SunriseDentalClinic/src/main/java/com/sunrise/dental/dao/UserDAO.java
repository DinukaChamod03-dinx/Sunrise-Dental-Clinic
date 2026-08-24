package com.sunrise.dental.dao;

import com.sunrise.dental.model.User;
import com.sunrise.dental.util.DBConnection;
import com.sunrise.dental.util.PasswordUtil;

import java.sql.*;

public class UserDAO {

    /** Validates login credentials. Returns the User on success, or null on failure. */
    public User validateLogin(String username, String plainPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String inputHash = PasswordUtil.hash(plainPassword);
                    if (storedHash.equals(inputHash)) {
                        User u = new User();
                        u.setId(rs.getInt("id"));
                        u.setUsername(rs.getString("username"));
                        u.setFullName(rs.getString("full_name"));
                        u.setRole(rs.getString("role"));
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

//username password

