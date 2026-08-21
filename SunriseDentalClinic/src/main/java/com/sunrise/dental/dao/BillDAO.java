package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;
import com.sunrise.dental.util.DBConnection;

import java.sql.*;

public class BillDAO {

    public boolean saveBill(Bill b) {
        String sql = "INSERT INTO bills (appointment_no, patient_name, treatment_type, treatment_cost, " +
                     "consultation_fee, total_amount, bill_date) VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getAppointmentNo());
            ps.setString(2, b.getPatientName());
            ps.setString(3, b.getTreatmentType());
            ps.setDouble(4, b.getTreatmentCost());
            ps.setDouble(5, b.getConsultationFee());
            ps.setDouble(6, b.getTotalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Bill getLatestBillForAppointment(String appointmentNo) {
        String sql = "SELECT * FROM bills WHERE appointment_no = ? ORDER BY bill_id DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Bill b = new Bill();
                    b.setBillId(rs.getInt("bill_id"));
                    b.setAppointmentNo(rs.getString("appointment_no"));
                    b.setPatientName(rs.getString("patient_name"));
                    b.setTreatmentType(rs.getString("treatment_type"));
                    b.setTreatmentCost(rs.getDouble("treatment_cost"));
                    b.setConsultationFee(rs.getDouble("consultation_fee"));
                    b.setTotalAmount(rs.getDouble("total_amount"));
                    b.setBillDate(String.valueOf(rs.getTimestamp("bill_date")));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
