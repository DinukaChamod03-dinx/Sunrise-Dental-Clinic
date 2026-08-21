package com.sunrise.dental.dao;

import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO {

    public List<Dentist> getAllDentists() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Dentist(rs.getInt("id"), rs.getString("name"), rs.getString("specialization")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
