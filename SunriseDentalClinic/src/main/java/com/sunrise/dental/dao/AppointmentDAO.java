package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    /** Generates a unique appointment number in the format APTyyyyMMdd-XXX (daily sequence). */
    public String generateAppointmentNo(String appointmentDate) {
        String datePart = appointmentDate.replace("-", "");
        String prefix = "APT" + datePart + "-";
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_no LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                if (rs.next()) count = rs.getInt(1);
                int next = count + 1;
                return prefix + String.format("%03d", next);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + System.currentTimeMillis();
    }

    //appointment number format in date 

    /** Returns true if the chosen dentist already has an active appointment at the same date & time. */
    public boolean isDoubleBooked(String dentistName, String appointmentDate, String appointmentTime, String excludeAppointmentNo) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE dentist_name = ? AND appointment_date = ? " +
                     "AND appointment_time = ? AND status <> 'CANCELLED' AND appointment_no <> ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dentistName);
            ps.setString(2, appointmentDate);
            ps.setString(3, appointmentTime);
            ps.setString(4, excludeAppointmentNo == null ? "" : excludeAppointmentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addAppointment(Appointment a) {
        String sql = "INSERT INTO appointments (appointment_no, patient_name, address, contact_number, " +
                     "dentist_name, treatment_type, appointment_date, appointment_time, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'SCHEDULED', NOW())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAppointmentNo());
            ps.setString(2, a.getPatientName());
            ps.setString(3, a.getAddress());
            ps.setString(4, a.getContactNumber());
            ps.setString(5, a.getDentistName());
            ps.setString(6, a.getTreatmentType());
            ps.setString(7, a.getAppointmentDate());
            ps.setString(8, a.getAppointmentTime());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Appointment getByAppointmentNo(String appointmentNo) {
        String sql = "SELECT * FROM appointments WHERE appointment_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Searches by patient name (partial match, case-insensitive). */
    public List<Appointment> searchByPatientName(String name) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE LOWER(patient_name) LIKE ? ORDER BY appointment_date DESC, appointment_time DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date DESC, appointment_time DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Returns today's scheduled appointments — used for the dashboard summary. */
    public List<Appointment> getTodaysAppointments() {
        List<Appointment> list = new ArrayList<>();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY appointment_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateAppointment(Appointment a) {
        String sql = "UPDATE appointments SET patient_name=?, address=?, contact_number=?, dentist_name=?, " +
                     "treatment_type=?, appointment_date=?, appointment_time=? WHERE appointment_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getPatientName());
            ps.setString(2, a.getAddress());
            ps.setString(3, a.getContactNumber());
            ps.setString(4, a.getDentistName());
            ps.setString(5, a.getTreatmentType());
            ps.setString(6, a.getAppointmentDate());
            ps.setString(7, a.getAppointmentTime());
            ps.setString(8, a.getAppointmentNo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Soft-cancels an appointment instead of deleting it, so records are never lost. */
    public boolean cancelAppointment(String appointmentNo) {
        String sql = "UPDATE appointments SET status='CANCELLED' WHERE appointment_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean markCompleted(String appointmentNo) {
        String sql = "UPDATE appointments SET status='COMPLETED' WHERE appointment_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentNo(rs.getString("appointment_no"));
        a.setPatientName(rs.getString("patient_name"));
        a.setAddress(rs.getString("address"));
        a.setContactNumber(rs.getString("contact_number"));
        a.setDentistName(rs.getString("dentist_name"));
        a.setTreatmentType(rs.getString("treatment_type"));
        a.setAppointmentDate(rs.getString("appointment_date"));
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setStatus(rs.getString("status"));
        a.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
        return a;
    }
}
