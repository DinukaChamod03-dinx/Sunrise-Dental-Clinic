package com.sunrise.dental.dao;

import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO {

    public List<Treatment> getAllTreatments() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments ORDER BY treatment_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Treatment(rs.getInt("id"), rs.getString("treatment_name"), rs.getDouble("cost")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Treatment getByName(String treatmentName) {
        String sql = "SELECT * FROM treatments WHERE treatment_name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, treatmentName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Treatment(rs.getInt("id"), rs.getString("treatment_name"), rs.getDouble("cost"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //add bill consulation fee

    /** Fixed consultation fee added to every bill. Kept in one place for easy editing. */
    public static final double CONSULTATION_FEE = 500.00;
}
